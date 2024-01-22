package br.com.runthebank.bankingaccount.service;

import br.com.runthebank.bankingaccount.dto.request.PaymentRequestDto;
import br.com.runthebank.bankingaccount.dto.request.UseRequestDto;
import br.com.runthebank.bankingaccount.dto.response.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface UserService {

    ResponseEntity<UserResponseDto> createAccount(UseRequestDto useRequestDto);

    ResponseEntity<AccountResponseDto> addAccount(UUID userId);

    ResponseEntity<PaymentResponseDto> makePayment(UUID userId, PaymentRequestDto paymentRequestDto);

    ResponseEntity<DepositResponseDto> depositToAccount(String branchNumber, String accountNumber, BigDecimal amount);

    ResponseEntity<List<AccountResponseDto>> getAllUsersAccounts();

    ResponseEntity<List<AccountInfoDto>> getUserAccounts(UUID clientId);
}
