package br.com.runthebank.bankingaccount.enums;

import lombok.Getter;

@Getter
public enum ResponseMessage {

    USER_CREATED_SUCCESSFULLY("User created successfully."),
    USER_FOUND_SUCCESSFULLY("User found."),
    TRANSACTION_SUCCESSFULLY_COMPLETED("Transaction successfully completed.");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }
}
