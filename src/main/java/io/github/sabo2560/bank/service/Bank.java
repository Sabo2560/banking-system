package io.github.sabo2560.bank.service;

import io.github.sabo2560.bank.config.ConfigReader;
import io.github.sabo2560.bank.exception.InsufficientFundsException;
import io.github.sabo2560.bank.model.Account;
import io.github.sabo2560.bank.model.CheckingAccount;
import io.github.sabo2560.bank.model.InterestBearing;
import io.github.sabo2560.bank.model.SavingsAccount;
import io.github.sabo2560.bank.exception.AccountNotFoundException;

import java.util.*;

public class Bank {

    private final Map<String, Account> accounts = new HashMap<>();
    private final String name;

    public Bank() {
        this.name = ConfigReader.get("bank.name");
    }

    public Account openSavingsAccount(String owner, double initialDeposit) {
        SavingsAccount savingAccount = new SavingsAccount(owner, initialDeposit);
        accounts.put(savingAccount.getAccountNumber(), savingAccount);
        return savingAccount;
    }

    public Account openCheckingAccount(String owner, double initialDeposit) {
        CheckingAccount checkingAccount = new CheckingAccount(owner, initialDeposit);
        accounts.put(checkingAccount.getAccountNumber(), checkingAccount);
        return checkingAccount;
    }

    public Account findAccount(String number) {

        Account account = accounts.get(number);

        if (account == null) {
            throw new AccountNotFoundException(number);
        }
        return account;
    }

    public String getName() {
        return name;
    }

    public void deposit(String number, double amount) {
        Account account = findAccount(number);
        account.deposit(amount);
    }

    public void withdraw(String number, double amount) throws InsufficientFundsException {
        Account account = findAccount(number);

        account.withdraw(amount);

    }

    public void transfer(String fromNumber, String toNumber, double amount) throws InsufficientFundsException {
        Account fromAccount = findAccount(fromNumber);
        Account toAccount = findAccount(toNumber);

        fromAccount.transferTo(toAccount, amount);

    }

    public int applyInterestToAll() {
        int count = 0;
        for (Account account : accounts.values()) {

            if (account instanceof InterestBearing ib) {
                double prior = account.getBalance();
                ib.applyInterest();
                double after = account.getBalance();
                if (after > prior) {
                    count++;
                }
            }
        }
        return count;
    }

    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>(accounts.values());
        list.sort(Comparator.comparing(Account::getAccountNumber));
        return list;
    }
}
