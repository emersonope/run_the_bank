package br.com.runthebank.bankingaccount.dto;

import br.com.runthebank.bankingaccount.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UseRequest {
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