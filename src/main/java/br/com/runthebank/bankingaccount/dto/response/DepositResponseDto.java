package br.com.runthebank.bankingaccount.dto.response;

import br.com.runthebank.bankingaccount.enums.AccountStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DepositResponseDto {
    private UUID account_id;
    private UUID user_id;
    private String accountName;
    private String branchNumber;
    private String accountNumber;
    private BigDecimal accountBalance;
    private AccountStatus status;
}
