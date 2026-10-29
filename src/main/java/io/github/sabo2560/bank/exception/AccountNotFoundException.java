package io.github.sabo2560.bank.exception;

import java.util.Locale;

public class AccountNotFoundException extends RuntimeException{
    private final String accountNumber;

    public AccountNotFoundException(String accountNumber) {
        super(String.format(Locale.US,"Account not found: %s", accountNumber));
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }
}
