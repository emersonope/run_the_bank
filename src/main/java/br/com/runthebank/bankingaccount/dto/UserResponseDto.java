package br.com.runthebank.bankingaccount.dto;

import br.com.runthebank.bankingaccount.enums.ResponseCode;
import br.com.runthebank.bankingaccount.enums.ResponseMessage;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserResponseDto {

    private ResponseCode responseCode;
    private ResponseMessage responseMessage;
    private List<AccountInfoDto> accountInfoDtoList;
    private boolean notificationSent;
}