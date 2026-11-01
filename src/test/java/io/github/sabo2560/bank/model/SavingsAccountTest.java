package io.github.sabo2560.bank.model;

import io.github.sabo2560.bank.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {

    @Test
    void openingWithValidDepositCreatesAccountWithOneDeposit() {
        SavingsAccount account = new SavingsAccount("Sabo", new BigDecimal("1000"));

        assertMoney("1000", account.getBalance());
        assertEquals(1, account.getTransactions().size());
        assertEquals(TransactionType.DEPOSIT, account.getTransactions().getFirst().getType());
        assertTrue(account.getAccountNumber().startsWith("ACC-"));
    }

    @Test
    void openingBelowMinimumIsRejected() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new SavingsAccount("Sabo", new BigDecimal("50")));

        assertEquals("Initial deposit must be at least 100.00", e.getMessage());
    }

    @Test
    void depositOfZeroOrNegativeIsRejected() {
        SavingsAccount account = new SavingsAccount("Sabo", new BigDecimal("1000"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> account.deposit(new BigDecimal("0")));
        assertEquals("Deposit amount must be greater than 0", e.getMessage());
        assertMoney("1000", account.getBalance());

        e = assertThrows(IllegalArgumentException.class,
                () -> account.deposit(new BigDecimal("-50")));
        assertEquals("Deposit amount must be greater than 0", e.getMessage());
        assertMoney("1000", account.getBalance());
    }

    @Test
    void withdrawingBelowMinimumBalanceIsRejected() {
        SavingsAccount account = new SavingsAccount("Sabo", new BigDecimal("1000"));

        InsufficientFundsException e = assertThrows(InsufficientFundsException.class,
                () -> account.withdraw(new BigDecimal("901")));

        assertMoney("900", e.getAvailable());
        assertMoney("901", e.getRequested());
        assertMoney("1000", account.getBalance());
    }

    @Test
    void withdrawingDownToMinimumBalanceSucceeds() throws InsufficientFundsException {
        SavingsAccount account = new SavingsAccount("Sabo", new BigDecimal("1000"));

        account.withdraw(new BigDecimal("900"));

        assertMoney("100", account.getBalance());
    }

    @Test
    void applyingInterestAddsInterestTransaction() {
        SavingsAccount account = new SavingsAccount("Sabo", new BigDecimal("1000"));

        account.applyInterest();

        assertMoney("1030", account.getBalance());
        List<Transaction> transactions = account.getTransactions();
        assertEquals(TransactionType.INTEREST, transactions.getLast().getType());
        assertMoney("30", transactions.getLast().getAmount());
    }

    private static void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "expected " + expected + " but was " + actual);
    }
}