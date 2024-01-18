package br.com.runthebank.bankingaccount.dto;

import br.com.runthebank.bankingaccount.enums.ResponseCode;
import br.com.runthebank.bankingaccount.enums.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private ResponseCode responseCode;
    private ResponseMessage responseMessage;
    private List<AccountInfo> accountInfoList;
}
