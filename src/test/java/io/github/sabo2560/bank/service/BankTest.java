package io.github.sabo2560.bank.service;

import io.github.sabo2560.bank.exception.AccountNotFoundException;
import io.github.sabo2560.bank.exception.InsufficientFundsException;
import io.github.sabo2560.bank.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    // tests use bank, savings, checking

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
        //same shape with AccountNotFoundException and "ACC-9999" as the target, then check savings is still 1000
        AccountNotFoundException e = assertThrows(AccountNotFoundException.class,
                () -> bank.transfer(savings.getAccountNumber(), "ACC-9999", 500));
        assertEquals("Account not found: ACC-9999", e.getMessage());
        assertEquals(1000, savings.getBalance(), 0.001);

    }

    @Test
    void accountsBelowReturnsOnlyMatchingAccounts() {
    }

    @Test
    void totalBalanceSumsAllAccounts() {

    }

    @Test
    void ownerNamesAreDistinctAndSorted(){

    }

    @Test
    void totalDepositedIncludesTransfersIn(){

    }
}