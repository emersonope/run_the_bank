package br.com.runthebank.bankingaccount.utils;

public class AccountUtils {

    public static String generateBranchNumber() {
        int min = 1000;
        int max = 9999;

        int randomNumber =  (int) Math.floor(Math.random() * (max - min + 1) + min);

        String branchRandomNumber = String.valueOf(randomNumber);

        StringBuilder branchNumber = new StringBuilder();

        return branchNumber.append(branchRandomNumber).toString();
    };

    public static String generateAccountNumber() {
        int min = 100000;
        int max = 999999;

        int randomNumber =  (int) Math.floor(Math.random() * (max - min + 1) + min);

        String accountRandomNumber = String.valueOf(randomNumber);

        StringBuilder accountNumber = new StringBuilder();

        return accountNumber.append(accountRandomNumber).toString();
    };

}