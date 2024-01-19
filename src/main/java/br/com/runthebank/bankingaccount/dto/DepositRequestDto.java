package br.com.runthebank.bankingaccount.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DepositRequestDto {
    private String branchNumber;
    private String accountNumber;
    private BigDecimal amount;
}

