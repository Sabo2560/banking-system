package io.github.sabo2560.bank.model;

import io.github.sabo2560.bank.config.ConfigReader;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class SavingsAccount extends Account implements InterestBearing {

    private final BigDecimal interestRate,minimumBalance;

    public SavingsAccount(String ownerName, BigDecimal initialDeposit) {
        super(ownerName, initialDeposit);
        this.interestRate = ConfigReader.getBigDecimal("savings.interest.rate");
        this.minimumBalance = ConfigReader.getBigDecimal("savings.minimum.balance");

        if (initialDeposit.compareTo(minimumBalance) < 0) {
            throw new IllegalArgumentException(String.format(Locale.US, "Initial deposit must be at least %.2f", minimumBalance));
        }

    }

    @Override
    protected BigDecimal getAvailableToWithdraw() {
        return getBalance().subtract(minimumBalance);
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }

    @Override
    public BigDecimal getInterestRate() {
        return interestRate;
    }

    @Override
    public void applyInterest() {
        BigDecimal interest = getBalance().multiply(interestRate).setScale(2, RoundingMode.HALF_UP);
        if (interest.compareTo(BigDecimal.ZERO) >0 ) {
            addTransaction(TransactionType.INTEREST,interest);
        }

    }
}
