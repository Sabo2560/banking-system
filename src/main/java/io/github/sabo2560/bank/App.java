package io.github.sabo2560.bank;

import io.github.sabo2560.bank.exception.AccountNotFoundException;
import io.github.sabo2560.bank.exception.ConfigException;
import io.github.sabo2560.bank.exception.InsufficientFundsException;
import io.github.sabo2560.bank.exception.TooManyAccountsException;
import io.github.sabo2560.bank.model.Account;
import io.github.sabo2560.bank.model.Transaction;
import io.github.sabo2560.bank.model.TransactionType;
import io.github.sabo2560.bank.service.Bank;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Bank bank;
        try {
            bank = new Bank();
        } catch (ConfigException e) {
            System.out.println("Configuration error: " + e.getMessage());
            System.exit(1);
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu(bank.getName());
                String choice = scanner.nextLine().trim();
                try {
                    switch (choice) {
                        case "1" -> openSavings(bank, scanner);
                        case "2" -> openChecking(bank, scanner);
                        case "3" -> deposit(bank, scanner);
                        case "4" -> withdraw(bank, scanner);
                        case "5" -> transfer(bank, scanner);
                        case "6" -> accountDetails(bank, scanner);
                        case "7" -> listAccounts(bank);
                        case "8" -> applyInterest(bank);
                        case "9" -> reports(bank, scanner);
                        case "0" -> running = false;
                        default -> System.out.println("Invalid option");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: please enter a valid number");
                } catch (AccountNotFoundException | TooManyAccountsException | IllegalArgumentException | InsufficientFundsException e) {
                    System.out.println("Error: " + e.getMessage());
                } finally {
                    System.out.println("--------------------");
                }
            }
        }
    }

    private static void printMenu(String name) {
        System.out.println("=== " + name + " ===");
        System.out.println("1. Open savings account");
        System.out.println("2. Open checking account");
        System.out.println("3. Deposit");
        System.out.println("4. Withdraw");
        System.out.println("5. Transfer");
        System.out.println("6. Account details and transactions");
        System.out.println("7. List all accounts");
        System.out.println("8. Apply interest to savings accounts");
        System.out.println("9. Reports");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
    }

    private static String readText(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static double readAmount(Scanner scanner, String prompt) {
        return Double.parseDouble(readText(scanner, prompt));
    }

    private static void openSavings(Bank bank, Scanner scanner) {
        String owner = readText(scanner, "Owner name: ");
        double deposit = readAmount(scanner, "Amount to Deposit: ");
        Account savingOpened = bank.openSavingsAccount(owner, deposit);
        System.out.println("Opened: " + savingOpened);
    }

    private static void openChecking(Bank bank, Scanner scanner) {
        String owner = readText(scanner, "Owner name: ");
        double deposit = readAmount(scanner, "Amount to Deposit: ");

        Account checkingOpened = bank.openCheckingAccount(owner, deposit);
        System.out.println("Opened: " + checkingOpened);
    }

    private static void deposit(Bank bank, Scanner scanner) {
        String number = readText(scanner, "Account Number: ");
        double deposit = readAmount(scanner, "Amount to Deposit: ");
        bank.deposit(number, deposit);
        System.out.printf(Locale.US, "New Balance: %.2f%n", bank.findAccount(number).getBalance());
    }

    private static void withdraw(Bank bank, Scanner scanner) throws InsufficientFundsException {
        String number = readText(scanner, "Account Number: ");
        double withdraw = readAmount(scanner, "Amount to withdraw: ");
        bank.withdraw(number, withdraw);
        System.out.printf(Locale.US, "New Balance: %.2f%n", bank.findAccount(number).getBalance());
    }

    private static void transfer(Bank bank, Scanner scanner) throws InsufficientFundsException {
        String fromNumber = readText(scanner, "Sender Account Number: ");
        String toNumber = readText(scanner, "Receivers Account Number: ");
        double transfer = readAmount(scanner, "Amount to transfer: ");
        bank.transfer(fromNumber, toNumber, transfer);
        System.out.printf(Locale.US, "%s new balance: %.2f%n", fromNumber, bank.findAccount(fromNumber).getBalance());
        System.out.printf(Locale.US, "%s new balance: %.2f%n", toNumber, bank.findAccount(toNumber).getBalance());
    }

    private static void accountDetails(Bank bank, Scanner scanner) {
        String number = readText(scanner, "Enter your Account Number: ");
        Account account = bank.findAccount(number);
        System.out.println(account);
        List<Transaction> transactions = account.getTransactions();

        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded yet");
        } else {
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        }
    }

    private static void listAccounts(Bank bank) {
        List<Account> allAccounts = bank.getAllAccounts();
        if (allAccounts.isEmpty()) {
            System.out.println("No Accounts Yet");
        } else {
            for (Account account : allAccounts) {
                System.out.println(account);
            }
        }
    }

    private static void applyInterest(Bank bank) {
        int count = bank.applyInterestToAll();
        System.out.println("Interest applied to " + count + " account(s)");
    }

    private static void reports(Bank bank, Scanner scanner) {
        System.out.println("--- Reports ---");
        System.out.println("1. Accounts below a balance");
        System.out.println("2. Total balance");
        System.out.println("3. Transactions of an account by type");
        System.out.println("4. Owner names");
        System.out.println("5. Total deposited to an account");
        String choice = readText(scanner, "Choose: ");

        switch (choice) {
            case "1" -> reportAccountsBelow(bank, scanner);
            case "2" -> System.out.printf(Locale.US, "Total balance: %.2f%n", bank.getTotalBalance());
            case "3" -> reportTransactionsByType(bank, scanner);
            case "4" -> reportOwnerNames(bank);
            case "5" -> reportTotalDeposited(bank, scanner);
            default -> System.out.println("Invalid option");
        }
    }

    private static void reportAccountsBelow(Bank bank, Scanner scanner) {
        double threshold = readAmount(scanner, "Show accounts below: ");
        List<Account> accounts = bank.getAccountsBelow(threshold);
        if (accounts.isEmpty()) {
            System.out.println("No accounts below that balance");
        } else {
            for (Account account : accounts) {
                System.out.println(account);
            }
        }
    }

    private static void reportTransactionsByType(Bank bank, Scanner scanner) {
        String number = readText(scanner, "Account Number: ");
        String typeText = readText(scanner, "Type (DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT, INTEREST): ");
        TransactionType type = TransactionType.valueOf(typeText.toUpperCase());
        List<Transaction> transactions = bank.getTransactions(number, type);
        if (transactions.isEmpty()) {
            System.out.println("No transactions of that type");
        } else {
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        }
    }

    private static void reportOwnerNames(Bank bank) {
        List<String> names = bank.getOwnerNames();
        if (names.isEmpty()) {
            System.out.println("No accounts yet");
        } else {
            for (String name : names) {
                System.out.println(name);
            }
        }
    }

    private static void reportTotalDeposited(Bank bank, Scanner scanner) {
        String number = readText(scanner, "Account Number: ");
        System.out.printf(Locale.US, "Total deposited to %s: %.2f%n", number, bank.getTotalDeposited(number));
    }
}