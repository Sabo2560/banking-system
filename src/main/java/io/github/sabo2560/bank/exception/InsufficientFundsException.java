package io.github.sabo2560.bank.exception;

import java.util.Locale;

public class InsufficientFundsException extends Exception{
    private final String accountNumber;
    private final double requested;
    private final double available;

    public InsufficientFundsException(String accountNumber, double requested, double available){
        super(String.format(Locale.US, "Insufficient funds in %s: requested %.2f, available %.2f", accountNumber, requested, available));
        this.accountNumber = accountNumber;
        this.requested = requested;
        this.available = available;
    }

    public String getAccountNumber(){
        return this.accountNumber;
    }
    public double getRequested(){
        return this.requested;
    }
    public double getAvailable(){
        return this.available;
    }
}
