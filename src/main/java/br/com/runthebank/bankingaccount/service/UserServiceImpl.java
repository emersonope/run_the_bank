package br.com.runthebank.bankingaccount.service;

import br.com.runthebank.bankingaccount.dto.AccountInfo;
import br.com.runthebank.bankingaccount.dto.UseRequest;
import br.com.runthebank.bankingaccount.dto.UserResponse;
import br.com.runthebank.bankingaccount.enums.AccountType;
import br.com.runthebank.bankingaccount.enums.ResponseCode;
import br.com.runthebank.bankingaccount.enums.ResponseMessage;
import br.com.runthebank.bankingaccount.excecoes.UserAlreadyExistsException;
import br.com.runthebank.bankingaccount.model.Account;
import br.com.runthebank.bankingaccount.model.User;
import br.com.runthebank.bankingaccount.repository.AccountRepository;
import br.com.runthebank.bankingaccount.repository.UserRepository;
import br.com.runthebank.bankingaccount.utils.AccountUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * Criando a conta - salvando os dados no bd
     * verificar se o usuario ja tem uma conta
     */
    @Override
    public ResponseEntity<UserResponse> createAccount(UseRequest useRequest) {
        try {
            validateAccountType(useRequest);

            String document = getDocument(useRequest);

            if (document != null && userRepository.existsByCpfOrCnpj(document, document)) {
                throw new UserAlreadyExistsException("User with the provided document already exists.");
            }

            Account newAccount = Account.builder()
                    .branchNumber(AccountUtils.generateBranchNumber())
                    .accountNumber(AccountUtils.generateAccountNumber())
                    .accountBalance(BigDecimal.ZERO)
                    .accountType(useRequest.getAccountType())
                    .build();

            newAccount = accountRepository.save(newAccount);

            User newUser = User.builder()
                    .firstName(useRequest.getFirstName())
                    .lastName(useRequest.getLastName())
                    .gender(useRequest.getGender())
                    .address(useRequest.getAddress())
                    .stateOfOriging(useRequest.getStateOfOriging())
                    .email(useRequest.getEmail())
                    .cpf(useRequest.getAccountType() == AccountType.PF ? useRequest.getCpf() : null)
                    .cnpj(useRequest.getAccountType() == AccountType.PJ ? useRequest.getCnpj() : null)
                    .phoneNumber(useRequest.getPhoneNumber())
                    .build();

            newUser.addAccount(newAccount);

            newUser = userRepository.save(newUser);

            final User finalUser = newUser;

            UserResponse userResponse = UserResponse.builder()
                    .responseCode(ResponseCode.SUCCESS)
                    .responseMessage(ResponseMessage.USER_CREATED_SUCCESSFULLY)
                    .accountInfoList(finalUser.getAccounts().stream()
                            .map(account -> AccountInfo.builder()
                                    .accountBalance(account.getAccountBalance())
                                    .branchNumber(account.getBranchNumber())
                                    .accountNumber(account.getAccountNumber())
                                    .accountName(finalUser.getFirstName() + " " + finalUser.getLastName())
                                    .cpf(finalUser.getCpf())
                                    .cnpj(finalUser.getCnpj())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            logger.error("There was an error while trying to create an User: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Server Error: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    private void validateAccountType(UseRequest useRequest) {
        if (useRequest.getAccountType() == null) {
            throw new IllegalArgumentException("Account type must be specified.");
        }

        if (useRequest.getAccountType() == AccountType.PF) {
            if (useRequest.getCnpj() != null) {
                throw new IllegalArgumentException("CNPJ should not be provided for a PF account.");
            }

            if (useRequest.getCpf() == null || useRequest.getCpf().isEmpty()) {
                throw new IllegalArgumentException("CPF is required for a PF account.");
            }
        }

        if (useRequest.getAccountType() == AccountType.PJ) {
            if (useRequest.getCpf() != null) {
                throw new IllegalArgumentException("CPF should not be provided for a PJ account.");
            }

            if (useRequest.getCnpj() == null || useRequest.getCnpj().isEmpty()) {
                throw new IllegalArgumentException("CNPJ is required for a PJ account.");
            }
        }
    }

    private String getDocument(UseRequest useRequest) {
        return useRequest.getAccountType() == AccountType.PF ? useRequest.getCpf() : useRequest.getCnpj();
    }

    @Override
    public ResponseEntity<UserResponse> addAccount(Long userId) {
        try {
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

            validateUser(existingUser);

            // Crie a nova conta vinculada ao usuário existente
            Account newAccount = Account.builder()
                    .branchNumber(AccountUtils.generateBranchNumber())
                    .accountNumber(AccountUtils.generateAccountNumber())
                    .accountBalance(BigDecimal.ZERO)
                    .accountType(existingUser.getAccounts().isEmpty() ? AccountType.PF : existingUser.getAccounts().get(0).getAccountType())
                    .user(existingUser)
                    .build();

            newAccount = accountRepository.save(newAccount);

            UserResponse userResponse = UserResponse.builder()
                    .responseCode(ResponseCode.SUCCESS)
                    .responseMessage(ResponseMessage.USER_CREATED_SUCCESSFULLY)
                    .accountInfoList(existingUser.getAccounts().stream()
                            .map(account -> AccountInfo.builder()
                                    .accountBalance(account.getAccountBalance())
                                    .branchNumber(account.getBranchNumber())
                                    .accountNumber(account.getAccountNumber())
                                    .accountName(existingUser.getFirstName() + " " + existingUser.getLastName())
                                    .cpf(existingUser.getCpf())
                                    .cnpj(existingUser.getCnpj())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Dados inválidos: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Erro interno do servidor: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
    }

    @Override
    public ResponseEntity<List<UserResponse>> getAllUsersAndAccounts() {
        try {
            List<User> allUsers = userRepository.findAll();

            List<UserResponse> userResponses = allUsers.stream()
                    .map(user -> UserResponse.builder()
                            .responseCode(ResponseCode.SUCCESS)
                            .responseMessage(ResponseMessage.USER_CREATED_SUCCESSFULLY)
                            .accountInfoList(user.getAccounts().stream()
                                    .map(account -> AccountInfo.builder()
                                            .accountBalance(account.getAccountBalance())
                                            .branchNumber(account.getBranchNumber())
                                            .accountNumber(account.getAccountNumber())
                                            .accountName(user.getFirstName() + " " + user.getLastName())
                                            .cpf(user.getCpf())
                                            .cnpj(user.getCnpj())
                                            .build())
                                    .collect(Collectors.toList()))
                            .build())
                    .collect(Collectors.toList());

            return new ResponseEntity<>(userResponses, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erro interno do servidor: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<List<AccountInfo>> getUserAccounts(Long clientId) {
        try {
            List<AccountInfo> userAccounts = userRepository.findById(clientId)
                    .map(user -> user.getAccounts().stream()
                            .map(account -> AccountInfo.builder()
                                    .accountBalance(account.getAccountBalance())
                                    .branchNumber(account.getBranchNumber())
                                    .accountNumber(account.getAccountNumber())
                                    .accountName(user.getFirstName() + " " + user.getLastName())
                                    .cpf(user.getCpf())
                                    .cnpj(user.getCnpj())
                                    .build())
                            .collect(Collectors.toList()))
                    .orElseThrow(() -> new IllegalArgumentException("Client not found with id: " + clientId));

            return new ResponseEntity<>(userAccounts, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Internal Server Error: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
