package br.com.runthebank.bankingaccount.enums;

import lombok.Getter;

@Getter
public enum ResponseMessage {

    USER_CREATED_SUCCESSFULLY("User created successfully.");

    private final String message;

    ResponseMessage(String message) {
        this.message = message;
    }
}
