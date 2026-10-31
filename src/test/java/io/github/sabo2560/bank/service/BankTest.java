package io.github.sabo2560.bank.service;

import io.github.sabo2560.bank.exception.AccountNotFoundException;
import io.github.sabo2560.bank.exception.InsufficientFundsException;
import io.github.sabo2560.bank.exception.TooManyAccountsException;
import io.github.sabo2560.bank.model.Account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;


class BankTest {

    private Bank bank;
    private Account savings;
    private Account checking;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        savings = bank.openSavingsAccount("Sabo", 1000);
        checking = bank.openCheckingAccount("Amine", 200);
    }


    @Test
    void findingUnknownAccountThrows() {
        AccountNotFoundException e = assertThrows(AccountNotFoundException.class,
                () -> bank.findAccount("ACC-9999"));
        assertEquals("Account not found: ACC-9999", e.getMessage());
    }

    @Test
    void transferMovesMoneyBetweenAccounts() throws InsufficientFundsException {
        bank.transfer(savings.getAccountNumber(), checking.getAccountNumber(), 300);
        assertEquals(700, savings.getBalance(), 0.001);
        assertEquals(500, checking.getBalance(), 0.001);
    }

    @Test
    void transferWithInsufficientFundsChangesNothing() {
        assertThrows(InsufficientFundsException.class,
                () -> bank.transfer(savings.getAccountNumber(), checking.getAccountNumber(), 5000));
        assertEquals(1000.00, savings.getBalance(), 0.001);
        assertEquals(200.00, checking.getBalance(), 0.001);

    }

    @Test
    void transferToUnknownAccountChangesNothing() {
        AccountNotFoundException e = assertThrows(AccountNotFoundException.class,
                () -> bank.transfer(savings.getAccountNumber(), "ACC-9999", 500));
        assertEquals("Account not found: ACC-9999", e.getMessage());
        assertEquals(1000, savings.getBalance(), 0.001);
    }

    @Test
    void accountsBelowReturnsOnlyMatchingAccounts() {
        assertEquals(List.of(checking), bank.getAccountsBelow(500));
    }

    @Test
    void totalBalanceSumsAllAccounts() {
        assertEquals(1200.0, bank.getTotalBalance(), 0.001);
    }

    @Test
    void ownerNamesAreDistinctAndSorted() {
        bank.openCheckingAccount("Sabo", 50);
        assertEquals(List.of("Amine", "Sabo"), bank.getOwnerNames());
    }

    @Test
    void totalDepositedIncludesTransfersIn() throws InsufficientFundsException {
        bank.transfer(savings.getAccountNumber(), checking.getAccountNumber(), 300);
        assertEquals(500, bank.getTotalDeposited(checking.getAccountNumber()), 0.001);
    }

    @Test
    void ownerCanOpenUpToTheLimit() {

        bank.openSavingsAccount("Sabo", 500);
        bank.openCheckingAccount("Sabo",500);

        assertEquals(4,bank.getAllAccounts().size());
    }

    @Test
    void openingBeyondTheLimitIsRejected() {

        bank.openSavingsAccount("Sabo",500);
        bank.openCheckingAccount("Sabo",500);

        TooManyAccountsException e = assertThrows(TooManyAccountsException.class,
                () -> bank.openCheckingAccount("Sabo",500));

       assertEquals("Sabo already has a maximum of 3 accounts", e.getMessage());
    }

    @Test
    void rejectedAccountIsNotStored() {

        bank.openSavingsAccount("Sabo",500);
        bank.openCheckingAccount("Sabo",500);

        assertThrows(TooManyAccountsException.class,
                () -> bank.openCheckingAccount("Sabo",500));

        assertEquals(4,bank.getAllAccounts().size());
    }
}