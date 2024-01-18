package br.com.runthebank.bankingaccount.service;

import br.com.runthebank.bankingaccount.dto.UseRequest;
import br.com.runthebank.bankingaccount.dto.UserResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<UserResponse> createAccount(UseRequest useRequest);
}
