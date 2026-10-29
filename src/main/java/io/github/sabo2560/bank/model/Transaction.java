package io.github.sabo2560.bank.model;

import java.time.LocalDateTime;
import java.util.Locale;

public class Transaction {
    private final TransactionType type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp = LocalDateTime.now();

    Transaction(TransactionType type, double amount, double balanceAfter){

        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    public TransactionType getType() {
        return type;
    }
    public double getBalanceAfter(){
        return balanceAfter;
    }
    public double getAmount(){
        return amount;
    }
    public LocalDateTime getTimestamp(){
        return timestamp ;
    }



    @Override
    public String toString() {
        return String.format(Locale.US, "%s %.2f | balance %.2f | %s", type,amount,balanceAfter,timestamp);
    }
}
