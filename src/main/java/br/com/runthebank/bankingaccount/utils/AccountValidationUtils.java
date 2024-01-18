package br.com.runthebank.bankingaccount.utils;

import br.com.runthebank.bankingaccount.dto.UseRequest;
import br.com.runthebank.bankingaccount.enums.AccountType;

public class AccountValidationUtils {

    public static void validateAccountType(UseRequest useRequest) {
        if (useRequest.getAccountType() == null) {
            throw new IllegalArgumentException("Account type must be specified.");
        }

        if (useRequest.getAccountType() == AccountType.PF) {
            if (useRequest.getCnpj() != null) {
                throw new IllegalArgumentException("CNPJ should not be provided for a PF account.");
            }

            if (useRequest.getCpf() == null || useRequest.getCpf().isEmpty()) {
                throw new IllegalArgumentException("CPF is required for a PF account.");
            }
        }

        if (useRequest.getAccountType() == AccountType.PJ) {
            if (useRequest.getCpf() != null) {
                throw new IllegalArgumentException("CPF should not be provided for a PJ account.");
            }

            if (useRequest.getCnpj() == null || useRequest.getCnpj().isEmpty()) {
                throw new IllegalArgumentException("CNPJ is required for a PJ account.");
            }
        }
    }
}
