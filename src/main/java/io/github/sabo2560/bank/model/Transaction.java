package io.github.sabo2560.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

public class Transaction {
    private final TransactionType type;
    private final BigDecimal amount;
    private final BigDecimal balanceAfter;
    private final LocalDateTime timestamp = LocalDateTime.now();

    Transaction(TransactionType type, BigDecimal amount, BigDecimal balanceAfter){

        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    public TransactionType getType() {
        return type;
    }
    public BigDecimal getBalanceAfter(){
        return balanceAfter;
    }
    public BigDecimal getAmount(){
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
