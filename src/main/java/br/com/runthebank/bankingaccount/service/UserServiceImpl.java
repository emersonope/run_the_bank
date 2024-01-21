package br.com.runthebank.bankingaccount.service;

import br.com.runthebank.bankingaccount.dto.AccountInfoDto;
import br.com.runthebank.bankingaccount.dto.PaymentRequestDto;
import br.com.runthebank.bankingaccount.dto.UseRequestDto;
import br.com.runthebank.bankingaccount.dto.UserResponseDto;
import br.com.runthebank.bankingaccount.enums.AccountStatus;
import br.com.runthebank.bankingaccount.enums.AccountType;
import br.com.runthebank.bankingaccount.enums.ResponseCode;
import br.com.runthebank.bankingaccount.enums.ResponseMessage;
import br.com.runthebank.bankingaccount.excecoes.InactiveAccountException;
import br.com.runthebank.bankingaccount.excecoes.InsufficientFundsException;
import br.com.runthebank.bankingaccount.excecoes.UserAlreadyExistsException;
import br.com.runthebank.bankingaccount.model.Account;
import br.com.runthebank.bankingaccount.model.User;
import br.com.runthebank.bankingaccount.repository.AccountRepository;
import br.com.runthebank.bankingaccount.repository.UserRepository;
import br.com.runthebank.bankingaccount.utils.AccountValidationUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountRepository accountRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final String NOTIFICATION_ENDPOINT = "https://run.mocky.io/v3/9769bf3a-b0b6-477a-9ff5-91f63010c9d3";

    /**
     * Criando a conta - salvando os dados no bd
     * verificar se o usuario ja tem uma conta
     */
    @Override
    public ResponseEntity<UserResponseDto> createAccount(UseRequestDto useRequestDto) {
        try {
            AccountValidationUtils.validateAccountType(useRequestDto);

            String document = getDocument(useRequestDto);

            if (document != null && userRepository.existsByCpfOrCnpj(document, document)) {
                throw new UserAlreadyExistsException("User with the provided document already exists.");
            }

            Account newAccount = Account.createNewAccount(useRequestDto.getAccountType());
            newAccount = accountRepository.save(newAccount);

            User newUser = User.createUserFromRequest(useRequestDto, newAccount);
            newUser.addAccount(newAccount);
            newUser = userRepository.save(newUser);

            final User finalUser = newUser;

            UserResponseDto userResponseDto = UserResponseDto.builder()
                    .responseCode(ResponseCode.SUCCESS)
                    .responseMessage(ResponseMessage.USER_CREATED_SUCCESSFULLY)
                    .accountInfoDtoList(finalUser.getAccounts().stream()
                            .map(account -> AccountInfoDto.builder()
                                    .accountBalance(account.getAccountBalance())
                                    .branchNumber(account.getBranchNumber())
                                    .accountNumber(account.getAccountNumber())
                                    .accountName(finalUser.getFirstName() + " " + finalUser.getLastName())
                                    .cpf(finalUser.getCpf())
                                    .cnpj(finalUser.getCnpj())
                                    .status(account.getStatus())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
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

    private String getDocument(UseRequestDto useRequestDto) {
        return useRequestDto.getAccountType() == AccountType.PF ? useRequestDto.getCpf() : useRequestDto.getCnpj();
    }

    @Override
    public ResponseEntity<UserResponseDto> addAccount(UUID userId) {
        try {
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

            validateUser(existingUser);

            String existingBranchNumber = existingUser.getAccounts().stream()
                    .filter(account -> account.getStatus() == AccountStatus.ATIVA)
                    .findFirst()
                    .map(Account::getBranchNumber)
                    .orElseThrow(() -> new IllegalArgumentException("User does not have an active account."));

            Account newAccount = Account.createAccountForUser(existingUser);
            newAccount.setBranchNumber(existingBranchNumber); //usar a agencia existente para criacao da cc
            accountRepository.save(newAccount);


            UserResponseDto userResponseDto = UserResponseDto.builder()
                    .responseCode(ResponseCode.SUCCESS)
                    .responseMessage(ResponseMessage.ACCOUNT_CREATED_SUCCESSFULLY)
                    .accountInfoDtoList(existingUser.getAccounts().stream()
                            .map(account -> AccountInfoDto.builder()
                                    .accountBalance(account.getAccountBalance())
                                    .branchNumber(account.getBranchNumber())
                                    .accountNumber(account.getAccountNumber())
                                    .accountName(existingUser.getFirstName() + " " + existingUser.getLastName())
                                    .cpf(existingUser.getCpf())
                                    .cnpj(existingUser.getCnpj())
                                    .status(account.getStatus())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Internal Server Error: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
    }

    @Override
    @Transactional
    public ResponseEntity<UserResponseDto> makePayment(UUID userId, PaymentRequestDto paymentRequestDto) {
        try {
            User sourceUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Source User not found with id: " + userId));

            validateUser(sourceUser);

            String sourceBranch = paymentRequestDto.getSourceBranch();
            String sourceAccountNumber = paymentRequestDto.getSourceAccountNumber();
            AccountType sourceAccountType = paymentRequestDto.getSourceAccountType();

            Account sourceAccount = getActiveAccount(sourceUser, sourceBranch, sourceAccountNumber, sourceAccountType);

            BigDecimal amount = paymentRequestDto.getAmount();
            String destinationBranch = paymentRequestDto.getDestinationBranch();
            String destinationAccountNumber = paymentRequestDto.getDestinationAccountNumber();
            AccountType destinationAccountType = paymentRequestDto.getAccountType();

            User targetUser = userRepository.findUserByAccount(destinationBranch, destinationAccountNumber, destinationAccountType)
                    .orElseThrow(() -> new IllegalArgumentException("Target User not found with account details."));

            validateUser(targetUser);

            Account targetAccount = getActiveAccount(targetUser, destinationBranch, destinationAccountNumber, destinationAccountType);

            if (sourceAccount.getId().equals(targetAccount.getId())) {
                throw new IllegalArgumentException("Source and target accounts must be different.");
            }

            if (sourceAccount.getAccountBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException("Insufficient funds in the source account.");
            }

            sourceAccount.setAccountBalance(sourceAccount.getAccountBalance().subtract(amount));
            targetAccount.setAccountBalance(targetAccount.getAccountBalance().add(amount));

            accountRepository.save(sourceAccount);
            accountRepository.save(targetAccount);

            boolean notificationSent = sendNotification();

            UserResponseDto userResponseDto = UserResponseDto.builder()
                    .responseCode(ResponseCode.SUCCESS)
                    .responseMessage(ResponseMessage.TRANSACTION_SUCCESSFULLY_COMPLETED)
                    .notificationSent(notificationSent)
                    .accountInfoDtoList(Arrays.asList(
                            buildAccountInfo(sourceAccount, sourceUser),
                            buildAccountInfo(targetAccount, targetUser)
                    ))
                    .build();

            return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
        } catch (InactiveAccountException | InsufficientFundsException e) {
            logger.error("Account error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Internal Server Error: {}", e.getMessage());
            throw new RuntimeException("Internal Server Error");
        }
    }

    private Account getActiveAccount(User user, String branchNumber, String accountNumber, AccountType accountType) {
        return user.getAccounts().stream()
                .filter(account -> account.getStatus() == AccountStatus.ATIVA
                        && account.getBranchNumber().equals(branchNumber)
                        && account.getAccountNumber().equals(accountNumber)
                        && account.getAccountType() == accountType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User does not have an active account with the specified details."));
    }

    private AccountInfoDto buildAccountInfo(Account account, User user) {
        return AccountInfoDto.builder()
                .accountBalance(account.getAccountBalance())
                .branchNumber(account.getBranchNumber())
                .accountNumber(account.getAccountNumber())
                .accountName(user.getFirstName() + " " + user.getLastName())
                .cpf(user.getCpf())
                .cnpj(user.getCnpj())
                .status(account.getStatus())
                .build();
    }


    private boolean sendNotification() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(NOTIFICATION_ENDPOINT, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return true;
            } else {
                throw new RuntimeException("Failed to send notification. HTTP Status Code: " + response.getStatusCodeValue());
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ResponseEntity<UserResponseDto> depositToAccount(String branchNumber, String accountNumber, BigDecimal amount) {
        try {

            if (branchNumber == null || accountNumber == null || amount == null) {
                throw new IllegalArgumentException("Branch number, account number, and amount are required.");
            }

            Account targetAccount = accountRepository.findByBranchNumberAndAccountNumber(branchNumber, accountNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Account not found with provided branch and account number."));

            if (targetAccount.getStatus() != AccountStatus.ATIVA) {
                throw new InactiveAccountException("Cannot deposit to an inactive account.");
            }

            targetAccount.setAccountBalance(targetAccount.getAccountBalance().add(amount));
            accountRepository.save(targetAccount);

            UserResponseDto userResponseDto = UserResponseDto.builder()
                    .responseCode(ResponseCode.SUCCESS)
                    .responseMessage(ResponseMessage.TRANSACTION_SUCCESSFULLY_COMPLETED)
                    .accountInfoDtoList(Collections.singletonList(
                            AccountInfoDto.builder()
                                    .accountBalance(targetAccount.getAccountBalance())
                                    .branchNumber(targetAccount.getBranchNumber())
                                    .accountNumber(targetAccount.getAccountNumber())
                                    .accountName(getAccountHolderName(targetAccount))
                                    .cpf(targetAccount.getUser().getCpf())
                                    .cnpj(targetAccount.getUser().getCnpj())
                                    .status(targetAccount.getStatus())
                                    .build()
                    ))
                    .build();

            sendNotification();

            return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
        } catch (IllegalArgumentException | InactiveAccountException e) {
            logger.error("Invalid data or inactive account: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Internal Server Error: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getAccountHolderName(Account account) {
        return account.getUser().getFirstName() + " " + account.getUser().getLastName();
    }

    private Account getActiveAccount(User user) {
        return user.getAccounts().stream()
                .filter(account -> account.getStatus() == AccountStatus.ATIVA)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User does not have an active account."));
    }

    @Override
    public ResponseEntity<List<UserResponseDto>> getAllUsersAndAccounts() {
        try {
            List<User> allUsers = userRepository.findAll();

            List<UserResponseDto> userResponsDtos = allUsers.stream()
                    .map(user -> UserResponseDto.builder()
                            .responseCode(ResponseCode.SUCCESS)
                            .responseMessage(ResponseMessage.USER_FOUND_SUCCESSFULLY)
                            .accountInfoDtoList(user.getAccounts().stream()
                                    .map(account -> AccountInfoDto.builder()
                                            .accountBalance(account.getAccountBalance())
                                            .branchNumber(account.getBranchNumber())
                                            .accountNumber(account.getAccountNumber())
                                            .accountName(user.getFirstName() + " " + user.getLastName())
                                            .cpf(user.getCpf())
                                            .cnpj(user.getCnpj())
                                            .status(account.getStatus())
                                            .build())
                                    .collect(Collectors.toList()))
                            .build())
                    .collect(Collectors.toList());

            return new ResponseEntity<>(userResponsDtos, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Server Error: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<List<AccountInfoDto>> getUserAccounts(UUID clientId) {
        try {
            List<AccountInfoDto> userAccounts = userRepository.findById(clientId)
                    .map(user -> user.getAccounts().stream()
                            .map(account -> AccountInfoDto.builder()
                                    .accountBalance(account.getAccountBalance())
                                    .branchNumber(account.getBranchNumber())
                                    .accountNumber(account.getAccountNumber())
                                    .accountName(user.getFirstName() + " " + user.getLastName())
                                    .cpf(user.getCpf())
                                    .cnpj(user.getCnpj())
                                    .status(account.getStatus())
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
