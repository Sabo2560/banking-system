package io.github.sabo2560.bank.service;

import io.github.sabo2560.bank.config.ConfigReader;
import io.github.sabo2560.bank.exception.InsufficientFundsException;
import io.github.sabo2560.bank.exception.TooManyAccountsException;
import io.github.sabo2560.bank.model.*;
import io.github.sabo2560.bank.exception.AccountNotFoundException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Bank {

    private final Map<String, Account> accounts = new HashMap<>();
    private final String name;

    public Bank() {
        this.name = ConfigReader.get("bank.name");
    }

    public Account openSavingsAccount(String owner, BigDecimal initialDeposit) {
        checkAccountLimit(owner);
        SavingsAccount savingAccount = new SavingsAccount(owner, initialDeposit);
        accounts.put(savingAccount.getAccountNumber(), savingAccount);
        return savingAccount;
    }

    public Account openCheckingAccount(String owner, BigDecimal initialDeposit) {
        checkAccountLimit(owner);
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

    public void deposit(String number, BigDecimal amount) {
        Account account = findAccount(number);
        account.deposit(amount);
    }

    public void withdraw(String number, BigDecimal amount) throws InsufficientFundsException {
        Account account = findAccount(number);

        account.withdraw(amount);

    }

    public void transfer(String fromNumber, String toNumber, BigDecimal amount) throws InsufficientFundsException {
        Account fromAccount = findAccount(fromNumber);
        Account toAccount = findAccount(toNumber);

        fromAccount.transferTo(toAccount, amount);

    }

    public int applyInterestToAll() {
        int count = 0;
        for (Account account : accounts.values()) {

            if (account instanceof InterestBearing ib) {
                BigDecimal prior = account.getBalance();
                ib.applyInterest();
                BigDecimal after = account.getBalance();
                if (after.compareTo(prior) >0) {
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

    public List<Account> getAccountsBelow(BigDecimal threshold) {
        List<Account> collectedList = accounts.values().stream()
                .filter(a -> a.getBalance().compareTo(threshold) < 0)
                .sorted(Comparator.comparing(Account::getBalance))
                .collect(Collectors.toList());
        return collectedList;
    }

    public BigDecimal getTotalBalance() {
        return accounts.values().stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Transaction> getTransactions(String number, TransactionType type) {
        return findAccount(number).getTransactions().stream().filter(t -> t.getType() == type).toList();
    }

    public List<String> getOwnerNames() {
        return accounts.values().stream()
                .map(Account::getOwnerName)
                .distinct()
                .sorted()
                .toList();
    }

    public BigDecimal getTotalDeposited(String number) {
        return findAccount(number).getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.DEPOSIT || t.getType() == TransactionType.TRANSFER_IN)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private void checkAccountLimit(String owner) {
        int limit =  ConfigReader.getInt("max.accounts.per.owner");
        long count = accounts.values().stream()
                .filter(a->a.getOwnerName().equals(owner))
                .count();
        if (count >= limit) {
            throw new TooManyAccountsException(owner,limit);
        }

    }

    public Map<String, List<Account>> getAccountsByType() {

        return accounts.values().stream().collect(Collectors.groupingBy(Account::getAccountType));
    }

}
