package br.com.runthebank.bankingaccount.controller;

import br.com.runthebank.bankingaccount.dto.request.DepositRequestDto;
import br.com.runthebank.bankingaccount.dto.request.PaymentRequestDto;
import br.com.runthebank.bankingaccount.dto.request.UseRequestDto;
import br.com.runthebank.bankingaccount.dto.response.*;
import br.com.runthebank.bankingaccount.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bankuser")
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @Operation(description = "Create an user and account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User and account were created"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "The server rejects the request, deeming it a client error.")
    })
    @PostMapping
    public ResponseEntity<UserResponseDto> createAccount(@RequestBody UseRequestDto useRequestDto) {
        return userService.createAccount(useRequestDto);
    }

    @Operation(description = "Create an account to an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account was created"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "The server rejects the request, deeming it a client error.")
    })
    @PostMapping("/{userId}/addAccount")
    public ResponseEntity<AccountResponseDto> addAccount(@PathVariable UUID userId) {
        return userService.addAccount(userId);
    }

    @Operation(description = "Make banking transaction between accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferred successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "400", description = "The server rejects the request, deeming it a client error.")
    })
    @PostMapping("/{userId}/makePayment")
    public ResponseEntity<PaymentResponseDto> makePayment(@PathVariable UUID userId, @RequestBody PaymentRequestDto paymentRequestDto) {

        return userService.makePayment(userId, paymentRequestDto);
    }

    @Operation(description = "Deposit an amount into an account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposited successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "400", description = "The server rejects the request, deeming it a client error.")
    })
    @PostMapping("/deposit")
    public ResponseEntity<DepositResponseDto> depositToAccount(@RequestBody DepositRequestDto depositRequestDTO) {
        String branchNumber = depositRequestDTO.getBranchNumber();
        String accountNumber = depositRequestDTO.getAccountNumber();
        BigDecimal amount = depositRequestDTO.getAmount();

        return userService.depositToAccount(branchNumber, accountNumber, amount);
    }

    @Operation(description = "Return the user data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return the user informatio & account"),
            @ApiResponse(responseCode = "400", description = "User do not exist")
    })
    @GetMapping("/allUsersAccounts")
    public ResponseEntity<List<AccountResponseDto>> getAllUsersAccounts() {
        return userService.getAllUsersAccounts();
    }

    @Operation(description = "Return the user data by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return the user info by ID"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "The server rejects the request, deeming it a client error.")
    })
    @GetMapping("/userAccounts/{clientId}")
    public ResponseEntity<List<AccountInfoDto>> getUserAccounts(@PathVariable UUID clientId) {
        return userService.getUserAccounts(clientId);
    }

}
