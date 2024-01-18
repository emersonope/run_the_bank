package br.com.runthebank.bankingaccount.dto;

import br.com.runthebank.bankingaccount.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {

    @NotNull(message = "Amount is required")
    private BigDecimal amount;
    private String cpf;
    private String cnpj;
    @NotBlank(message = "Destination branch is required")
    private String destinationBranch;
    @NotBlank(message = "Destination account number is required")
    private String destinationAccountNumber;
    @NotNull(message = "Account type is required")
    private AccountType accountType;
}
