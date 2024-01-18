package br.com.runthebank.bankingaccount.service;

import br.com.runthebank.bankingaccount.dto.AccountInfo;
import br.com.runthebank.bankingaccount.dto.UseRequest;
import br.com.runthebank.bankingaccount.dto.UserResponse;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {

    ResponseEntity<UserResponse> createAccount(UseRequest useRequest);

    ResponseEntity<UserResponse> addAccount(Long userId);

    ResponseEntity<UserResponse> makePayment(Long sourceUserId, Long targetUserId, BigDecimal amount, String destinationBranch, String destinationAccountNumber, String cpf, String cnpj);

    ResponseEntity<UserResponse> depositToAccount(String branchNumber, String accountNumber, BigDecimal amount);

    ResponseEntity<List<UserResponse>> getAllUsersAndAccounts();

    ResponseEntity<List<AccountInfo>> getUserAccounts(Long clientId);
}
