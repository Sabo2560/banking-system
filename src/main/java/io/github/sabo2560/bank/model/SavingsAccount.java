package io.github.sabo2560.bank.model;

import io.github.sabo2560.bank.config.ConfigReader;

import java.util.Locale;

public class SavingsAccount extends Account implements InterestBearing {

    private final double interestRate,minimumBalance;

    public SavingsAccount(String ownerName, double initialDeposit) {
        super(ownerName, initialDeposit);
        this.interestRate = ConfigReader.getDouble("savings.interest.rate");
        this.minimumBalance = ConfigReader.getDouble("savings.minimum.balance");

        if (initialDeposit < minimumBalance) {
            throw new IllegalArgumentException(String.format(Locale.US, "Initial deposit must be at least %.2f", minimumBalance));
        }

    }

    @Override
    protected double getAvailableToWithdraw() {
        return getBalance() - minimumBalance;
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }

    @Override
    public double getInterestRate() {
        return interestRate;
    }

    @Override
    public void applyInterest() {
        double interest = interestRate * getBalance();
        if (interest > 0 ) {
            addTransaction(TransactionType.INTEREST,interest);
        }

    }
}
