package br.com.runthebank.bankingaccount.excecoes;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String message) {
        super(message);
    }
}

