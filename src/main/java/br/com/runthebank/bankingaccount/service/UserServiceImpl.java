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
//        User saveUser = userRepository.save(newUser);

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
            logger.error("Erro ao criar conta do usuário: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            logger.error("Dados Invalidos: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("??: {}", e.getMessage());
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

}
