package io.github.sabo2560.bank.model;

import io.github.sabo2560.bank.exception.InsufficientFundsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public abstract class Account {

    private static final int FIRST_NUMBER = 1001;
    private static int nextNumber = FIRST_NUMBER;
    private final String accountNumber;
    private final String ownerName;
    private double balance;
    private final List<Transaction> transactions = new ArrayList<Transaction>();

    protected Account(String ownerName, double initialDeposit) {

        if ((ownerName == null) || (ownerName.isBlank())) {
            throw new IllegalArgumentException("Owner name must be filled!");
        } else if (initialDeposit < 0) {
            throw new IllegalArgumentException("Initial Deposit can't be negative!");
        }
        this.ownerName = ownerName;
        this.accountNumber = "ACC-" + nextNumber++;
        if (initialDeposit > 0) {
            addTransaction(TransactionType.DEPOSIT, initialDeposit);
        }
    }

    public static int getTotalAccountsCreated() {
        return nextNumber - FIRST_NUMBER;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public double getBalance() {
        return balance;
    }

    protected abstract double getAvailableToWithdraw();

    public abstract String getAccountType();

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than 0");
        } else {
            addTransaction(TransactionType.DEPOSIT, amount);
        }
    }

    public void withdraw(double amount) throws InsufficientFundsException {

        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than 0");
        }

        double available = getAvailableToWithdraw();

        if (amount > available) {
            throw new InsufficientFundsException(accountNumber, amount, available);
        }
        addTransaction(TransactionType.WITHDRAWAL, amount);

    }

    protected void addTransaction(TransactionType type, double amount) {
        switch (type) {
            case DEPOSIT, TRANSFER_IN, INTEREST -> balance += amount;
            case WITHDRAWAL, TRANSFER_OUT -> balance -= amount;
        }
        transactions.add(new Transaction(type, amount, balance));
    }

    public void transferTo(Account target, double amount) throws InsufficientFundsException {
        if (target == null || target == this) {
            throw new IllegalArgumentException("Transfer account is invalid");
        } else if (amount <= 0) {
            throw new IllegalArgumentException("Transfer Amount Should be greater than 0");
        }

        double available = getAvailableToWithdraw();

        if (amount > available) {
            throw new InsufficientFundsException(accountNumber, amount, available);
        }
        addTransaction(TransactionType.TRANSFER_OUT, amount);
        target.addTransaction(TransactionType.TRANSFER_IN, amount);
    }

    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "%s | %s | %s | %.2f", accountNumber, getAccountType(), ownerName, balance);

    }


}
