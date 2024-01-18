package br.com.runthebank.bankingaccount.controller;

import br.com.runthebank.bankingaccount.dto.AccountInfo;
import br.com.runthebank.bankingaccount.dto.PaymentRequest;
import br.com.runthebank.bankingaccount.dto.UseRequest;
import br.com.runthebank.bankingaccount.dto.UserResponse;
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

    @PostMapping("/{sourceUserId}/makePayment/{targetUserId}")
    public ResponseEntity<UserResponse> makePayment(
            @PathVariable Long sourceUserId,
            @PathVariable Long targetUserId,
            @RequestBody PaymentRequest paymentRequest) {

        BigDecimal amount = paymentRequest.getAmount();
        String destinationBranch = paymentRequest.getDestinationBranch();
        String destinationAccountNumber = paymentRequest.getDestinationAccountNumber();
        AccountType destinationAccountType = paymentRequest.getAccountType();

        String cpf = null;
        String cnpj = null;

        if (destinationAccountType == AccountType.PF) {
            cpf = paymentRequest.getCpf();
        } else if (destinationAccountType == AccountType.PJ) {
            cnpj = paymentRequest.getCnpj();
        }
        ResponseEntity<UserResponse> response = userService.makePayment(sourceUserId, targetUserId, amount, destinationBranch, destinationAccountNumber, cpf, cnpj);
        return response;
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
