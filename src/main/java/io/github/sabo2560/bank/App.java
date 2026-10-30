package io.github.sabo2560.bank;

import io.github.sabo2560.bank.exception.AccountNotFoundException;
import io.github.sabo2560.bank.exception.ConfigException;
import io.github.sabo2560.bank.model.Account;
import io.github.sabo2560.bank.service.Bank;

import java.util.List;
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

                        case "7" -> listAccounts(bank);
                        // ... cases 2 to 9
                        case "0" -> running = false;
                        default -> System.out.println("Invalid option");
                    }
                } catch (NumberFormatException e) {
                System.out.println("Error: please enter a valid number");
            } catch (AccountNotFoundException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } finally {
                    System.out.println("--------------------");
                }
            }
        }
    }

    private static void openSavings(Bank bank, Scanner scanner) {
        String owner = readText(scanner, "Owner name: ");
        double deposit = readAmount(scanner,"Amount to Deposit: ");

        Account savingOpened =  bank.openSavingsAccount(owner,deposit);
        System.out.println("Opened: " + savingOpened);
    }

    private static String readText(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static double readAmount(Scanner scanner, String prompt) {
        return Double.parseDouble(readText(scanner, prompt));
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

    private static void listAccounts(Bank bank){
        List<Account> allAccounts = bank.getAllAccounts();
        if (allAccounts.isEmpty()) {
            System.out.println("No Accounts Yet");
        }else {
            for (Account account : allAccounts) {
                System.out.println(account);
            }
        }
    }
}