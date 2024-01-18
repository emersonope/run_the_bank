package br.com.runthebank.bankingaccount.excecoes;

public class InactiveAccountException extends RuntimeException {

    public InactiveAccountException(String message) {
        super(message);
    }
}
