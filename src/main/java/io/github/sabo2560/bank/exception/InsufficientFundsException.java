package io.github.sabo2560.bank.exception;

import java.math.BigDecimal;
import java.util.Locale;

public class InsufficientFundsException extends Exception{
    private final String accountNumber;
    private final BigDecimal requested;
    private final BigDecimal available;

    public InsufficientFundsException(String accountNumber, BigDecimal requested, BigDecimal available){
        super(String.format(Locale.US, "Insufficient funds in %s: requested %.2f, available %.2f", accountNumber, requested, available));
        this.accountNumber = accountNumber;
        this.requested = requested;
        this.available = available;
    }

    public String getAccountNumber(){
        return this.accountNumber;
    }
    public BigDecimal getRequested(){
        return this.requested;
    }
    public BigDecimal getAvailable(){
        return this.available;
    }
}
