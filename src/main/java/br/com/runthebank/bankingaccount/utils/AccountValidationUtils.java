package br.com.runthebank.bankingaccount.utils;

import br.com.runthebank.bankingaccount.dto.UseRequestDto;
import br.com.runthebank.bankingaccount.enums.AccountType;

public class AccountValidationUtils {

    public static void validateAccountType(UseRequestDto useRequestDto) {
        if (useRequestDto.getAccountType() == null) {
            throw new IllegalArgumentException("Account type must be specified.");
        }

        if (useRequestDto.getAccountType() == AccountType.PF) {
            if (useRequestDto.getCnpj() != null) {
                throw new IllegalArgumentException("CNPJ should not be provided for a PF account.");
            }

            if (useRequestDto.getCpf() == null || useRequestDto.getCpf().isEmpty()) {
                throw new IllegalArgumentException("CPF is required for a PF account.");
            }
        }

        if (useRequestDto.getAccountType() == AccountType.PJ) {
            if (useRequestDto.getCpf() != null) {
                throw new IllegalArgumentException("CPF should not be provided for a PJ account.");
            }

            if (useRequestDto.getCnpj() == null || useRequestDto.getCnpj().isEmpty()) {
                throw new IllegalArgumentException("CNPJ is required for a PJ account.");
            }
        }
    }
}
