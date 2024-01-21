package br.com.runthebank.bankingaccount.service;

import br.com.runthebank.bankingaccount.dto.AccountInfoDto;
import br.com.runthebank.bankingaccount.dto.PaymentRequestDto;
import br.com.runthebank.bankingaccount.dto.UseRequestDto;
import br.com.runthebank.bankingaccount.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface UserService {

    ResponseEntity<UserResponseDto> createAccount(UseRequestDto useRequestDto);

    ResponseEntity<UserResponseDto> addAccount(UUID userId);

    ResponseEntity<UserResponseDto> makePayment(UUID userId, PaymentRequestDto paymentRequestDto);

    ResponseEntity<UserResponseDto> depositToAccount(String branchNumber, String accountNumber, BigDecimal amount);

    ResponseEntity<List<UserResponseDto>> getAllUsersAndAccounts();

    ResponseEntity<List<AccountInfoDto>> getUserAccounts(UUID clientId);
}
