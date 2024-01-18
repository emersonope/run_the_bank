package br.com.runthebank.bankingaccount.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DepositRequestDTO {
    private String branchNumber;
    private String accountNumber;
    private BigDecimal amount;
}

