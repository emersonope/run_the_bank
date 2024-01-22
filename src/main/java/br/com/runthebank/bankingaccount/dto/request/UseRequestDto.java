package br.com.runthebank.bankingaccount.dto.request;

import br.com.runthebank.bankingaccount.enums.AccountType;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UseRequestDto {
    private String firstName;
    private String lastName;
    private String gender;
    private String address;
    private String stateOfOriging;
    private String email;
    private String cpf;
    private String cnpj;
    private String phoneNumber;
    private AccountType accountType;
}