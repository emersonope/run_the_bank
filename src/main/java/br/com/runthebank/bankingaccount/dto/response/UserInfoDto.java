package br.com.runthebank.bankingaccount.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInfoDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String gender;
    private String address;
    private String stateOfOriging;
    private String email;
    private String cpf;
    private String cnpj;
    private String phoneNumber;
    private LocalDateTime createdAt;

}
