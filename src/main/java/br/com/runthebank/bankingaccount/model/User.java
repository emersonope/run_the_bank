package br.com.runthebank.bankingaccount.model;

import br.com.runthebank.bankingaccount.dto.UseRequest;
import br.com.runthebank.bankingaccount.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private String address;
    private String stateOfOriging;
    private String email;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts;
    @CPF(message = "Invalid CPF")
    @Column(unique = true)
    private String cpf;
    @CNPJ(message = "Invalid CNPJ")
    @Column(unique = true)
    private String cnpj;
    private String phoneNumber;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime modifiedAt;

    public static User createUserFromRequest(UseRequest useRequest, Account newAccount) {
        return User.builder()
                .firstName(useRequest.getFirstName())
                .lastName(useRequest.getLastName())
                .gender(useRequest.getGender())
                .address(useRequest.getAddress())
                .stateOfOriging(useRequest.getStateOfOriging())
                .email(useRequest.getEmail())
                .cpf(useRequest.getAccountType() == AccountType.PF ? useRequest.getCpf() : null)
                .cnpj(useRequest.getAccountType() == AccountType.PJ ? useRequest.getCnpj() : null)
                .phoneNumber(useRequest.getPhoneNumber())
                .build();
    }
    public void addAccount(Account account) {
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        accounts.add(account);
        account.setUser(this);
    }
}
