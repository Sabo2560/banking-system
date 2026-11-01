package io.github.sabo2560.bank.model;

import io.github.sabo2560.bank.config.ConfigReader;

import java.math.BigDecimal;

public class CheckingAccount extends Account{
    private final BigDecimal overdraftLimit = ConfigReader.getBigDecimal("checking.overdraft.limit");

    public CheckingAccount(String ownerName, BigDecimal initialDeposit){
        super(ownerName,initialDeposit);
    }

    @Override
    protected BigDecimal getAvailableToWithdraw() {
        return getBalance().add(overdraftLimit);
    }

    @Override
    public String getAccountType() {
        return "CHECKING";
    }
}
