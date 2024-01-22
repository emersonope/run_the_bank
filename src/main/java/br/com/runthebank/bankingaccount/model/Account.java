package br.com.runthebank.bankingaccount.model;

import br.com.runthebank.bankingaccount.enums.AccountStatus;
import br.com.runthebank.bankingaccount.enums.AccountType;
import br.com.runthebank.bankingaccount.utils.AccountUtils;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID account_id;
    private String branchNumber;
    private String accountNumber;
    private BigDecimal accountBalance;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private AccountStatus status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime modifiedAt;

    public static Account createNewAccount(AccountType accountType) {
        return Account.builder()
                .branchNumber(AccountUtils.generateBranchNumber())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .accountType(accountType)
                .status(AccountStatus.ATIVA)
                .build();
    }


    public static Account createAccountForUser(User user) {
        AccountType accountType = user.getAccounts().isEmpty() ? AccountType.PF : user.getAccounts().get(0).getAccountType();

        return Account.builder()
                .branchNumber(AccountUtils.generateBranchNumber())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .accountType(accountType)
                .user(user)
                .status(AccountStatus.ATIVA)
                .build();
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + account_id +
                ", branchNumber='" + branchNumber + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountBalance=" + accountBalance +
                ", accountType=" + accountType +
                ", user=" + user +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                '}';
    }
}
