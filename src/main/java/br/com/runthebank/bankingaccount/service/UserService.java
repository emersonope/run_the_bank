package br.com.runthebank.bankingaccount.service;

import br.com.runthebank.bankingaccount.dto.AccountInfoDto;
import br.com.runthebank.bankingaccount.dto.PaymentRequestDto;
import br.com.runthebank.bankingaccount.dto.UseRequestDto;
import br.com.runthebank.bankingaccount.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

public interface UserService {

    ResponseEntity<UserResponseDto> createAccount(UseRequestDto useRequestDto);

    ResponseEntity<UserResponseDto> addAccount(Long userId);

    ResponseEntity<UserResponseDto> makePayment(Long userId, PaymentRequestDto paymentRequestDto);

    ResponseEntity<UserResponseDto> depositToAccount(String branchNumber, String accountNumber, BigDecimal amount);

    ResponseEntity<List<UserResponseDto>> getAllUsersAndAccounts();

    ResponseEntity<List<AccountInfoDto>> getUserAccounts(Long clientId);
}
