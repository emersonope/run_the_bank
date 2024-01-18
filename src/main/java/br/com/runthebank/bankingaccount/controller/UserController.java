package br.com.runthebank.bankingaccount.controller;

import br.com.runthebank.bankingaccount.dto.AccountInfo;
import br.com.runthebank.bankingaccount.dto.UseRequest;
import br.com.runthebank.bankingaccount.dto.UserResponse;
import br.com.runthebank.bankingaccount.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bankuser")
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @PostMapping
    public ResponseEntity<UserResponse> createAccount(@RequestBody UseRequest useRequest) {
        return userService.createAccount(useRequest);
    }

    @PostMapping("/{userId}/addAccount")
    public ResponseEntity<UserResponse> addAccount(@PathVariable Long userId) {
        return userService.addAccount(userId);
    }

    @GetMapping("/allUsersAndAccounts")
    public ResponseEntity<List<UserResponse>> getAllUsersAndAccounts() {
        return userService.getAllUsersAndAccounts();
    }

    @GetMapping("/userAccounts/{clientId}")
    public ResponseEntity<List<AccountInfo>> getUserAccounts(@PathVariable Long clientId) {
        return userService.getUserAccounts(clientId);
    }

}
