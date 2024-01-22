package br.com.runthebank.bankingaccount.model;

import br.com.runthebank.bankingaccount.dto.request.UseRequestDto;
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
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
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

    public static User createUserFromRequest(UseRequestDto useRequestDto, Account newAccount) {
        return User.builder()
                .firstName(useRequestDto.getFirstName())
                .lastName(useRequestDto.getLastName())
                .gender(useRequestDto.getGender())
                .address(useRequestDto.getAddress())
                .stateOfOriging(useRequestDto.getStateOfOriging())
                .email(useRequestDto.getEmail())
                .cpf(useRequestDto.getAccountType() == AccountType.PF ? useRequestDto.getCpf() : null)
                .cnpj(useRequestDto.getAccountType() == AccountType.PJ ? useRequestDto.getCnpj() : null)
                .phoneNumber(useRequestDto.getPhoneNumber())
                .build();
    }
    public void addAccount(Account account) {
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        accounts.add(account);
        account.setUser(this);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", stateOfOriging='" + stateOfOriging + '\'' +
                ", email='" + email + '\'' +
                ", accounts=" + accounts +
                ", cpf='" + cpf + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(gender, user.gender) &&
                Objects.equals(address, user.address) &&
                Objects.equals(stateOfOriging, user.stateOfOriging) &&
                Objects.equals(email, user.email) &&
                Objects.equals(cpf, user.cpf) &&
                Objects.equals(cnpj, user.cnpj) &&
                Objects.equals(phoneNumber, user.phoneNumber) &&
                Objects.equals(createdAt, user.createdAt) &&
                Objects.equals(modifiedAt, user.modifiedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, gender, address, stateOfOriging, email, cpf, cnpj, phoneNumber, createdAt, modifiedAt);
    }

}