package br.com.runthebank.bankingaccount.controller;

import br.com.runthebank.bankingaccount.dto.*;
import br.com.runthebank.bankingaccount.enums.AccountType;
import br.com.runthebank.bankingaccount.model.Account;
import br.com.runthebank.bankingaccount.service.UserServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/{userId}/makePayment")
    public ResponseEntity<UserResponse> makePayment(@PathVariable Long userId, @RequestBody PaymentRequest paymentRequest) {

        return userService.makePayment(userId, paymentRequest);
    }

    @PostMapping("/deposit")
    public ResponseEntity<UserResponse> depositToAccount(@RequestBody DepositRequestDTO depositRequestDTO) {
        String branchNumber = depositRequestDTO.getBranchNumber();
        String accountNumber = depositRequestDTO.getAccountNumber();
        BigDecimal amount = depositRequestDTO.getAmount();

        return userService.depositToAccount(branchNumber, accountNumber, amount);
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
