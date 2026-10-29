package io.github.sabo2560.bank.model;

import io.github.sabo2560.bank.config.ConfigReader;

public class CheckingAccount extends Account{
    private final double overdraftLimit = ConfigReader.getDouble("checking.overdraft.limit");

    public CheckingAccount(String ownerName, double initialDeposit){
        super(ownerName,initialDeposit);
    }

    @Override
    protected double getAvailableToWithdraw() {
        return getBalance() + overdraftLimit;
    }

    @Override
    public String getAccountType() {
        return "CHECKING";
    }
}
