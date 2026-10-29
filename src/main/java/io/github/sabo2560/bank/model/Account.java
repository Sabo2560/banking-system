package io.github.sabo2560.bank.model;

import java.util.ArrayList;
import java.util.List;


public abstract class Account {

    private static final int FIRST_Number = 1001;
    private static int nextNumber = FIRST_Number;
    private final String accountNumber;
    private final String ownerName;
    private double balance;
    private final List<Transaction> transactions = new ArrayList<Transaction>();

    protected Account(String ownerName, double initialDeposit){

        if ((ownerName == null) || (ownerName.isBlank())) {
            throw new IllegalArgumentException("Owner name must be filled!");
        } else if (initialDeposit < 0) {
            throw new IllegalArgumentException("Initial Deposit can't be negative!");
        }
        this.ownerName = ownerName;
        this.accountNumber = "ACC-" + nextNumber++;
        if (initialDeposit >0) {
            addTransaction(TransactionType.DEPOSIT, initialDeposit);
        }
    }

    public static int getTotalAccountsCreated() {
        return nextNumber - FIRST_Number;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }
    public double getBalance(){
        return balance;
    }
}
