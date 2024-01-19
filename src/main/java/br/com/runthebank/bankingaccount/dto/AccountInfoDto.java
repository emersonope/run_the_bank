package br.com.runthebank.bankingaccount.dto;

import br.com.runthebank.bankingaccount.enums.AccountStatus;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountInfoDto {

    private String accountName;
    private String branchNumber;
    private String accountNumber;
    private BigDecimal accountBalance;
    private String cpf;
    private String cnpj;
    private AccountStatus status;
}
