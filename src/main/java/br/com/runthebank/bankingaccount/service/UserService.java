package br.com.runthebank.bankingaccount.service;

import br.com.runthebank.bankingaccount.dto.AccountInfo;
import br.com.runthebank.bankingaccount.dto.UseRequest;
import br.com.runthebank.bankingaccount.dto.UserResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {

    ResponseEntity<UserResponse> createAccount(UseRequest useRequest);

    ResponseEntity<UserResponse> addAccount(Long userId);

    ResponseEntity<List<UserResponse>> getAllUsersAndAccounts();

    ResponseEntity<List<AccountInfo>> getUserAccounts(Long clientId);
}
