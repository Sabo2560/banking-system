package io.github.sabo2560.bank.service;

import io.github.sabo2560.bank.exception.AccountNotFoundException;
import io.github.sabo2560.bank.exception.InsufficientFundsException;
import io.github.sabo2560.bank.exception.TooManyAccountsException;
import io.github.sabo2560.bank.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BankTest {

    private Bank bank;
    private Account savings;
    private Account checking;

    @BeforeEach
    void setUp() {
        bank = new Bank();
        savings = bank.openSavingsAccount("Sabo", new BigDecimal("1000"));
        checking = bank.openCheckingAccount("Amine", new BigDecimal("200"));
    }

    @Test
    void findingUnknownAccountThrows() {
        AccountNotFoundException e = assertThrows(AccountNotFoundException.class,
                () -> bank.findAccount("ACC-9999"));
        assertEquals("Account not found: ACC-9999", e.getMessage());
    }

    @Test
    void transferMovesMoneyBetweenAccounts() throws InsufficientFundsException {
        bank.transfer(savings.getAccountNumber(), checking.getAccountNumber(), new BigDecimal("300"));
        assertMoney("700", savings.getBalance());
        assertMoney("500", checking.getBalance());
    }

    @Test
    void transferWithInsufficientFundsChangesNothing() {
        assertThrows(InsufficientFundsException.class,
                () -> bank.transfer(savings.getAccountNumber(), checking.getAccountNumber(), new BigDecimal("5000")));
        assertMoney("1000", savings.getBalance());
        assertMoney("200", checking.getBalance());
    }

    @Test
    void transferToUnknownAccountChangesNothing() {
        AccountNotFoundException e = assertThrows(AccountNotFoundException.class,
                () -> bank.transfer(savings.getAccountNumber(), "ACC-9999", new BigDecimal("500")));
        assertEquals("Account not found: ACC-9999", e.getMessage());
        assertMoney("1000", savings.getBalance());
    }

    @Test
    void accountsBelowReturnsOnlyMatchingAccounts() {
        assertEquals(List.of(checking), bank.getAccountsBelow(new BigDecimal("500")));
    }

    @Test
    void totalBalanceSumsAllAccounts() {
        assertMoney("1200", bank.getTotalBalance());
    }

    @Test
    void ownerNamesAreDistinctAndSorted() {
        bank.openCheckingAccount("Sabo", new BigDecimal("50"));
        assertEquals(List.of("Amine", "Sabo"), bank.getOwnerNames());
    }

    @Test
    void totalDepositedIncludesTransfersIn() throws InsufficientFundsException {
        bank.transfer(savings.getAccountNumber(), checking.getAccountNumber(), new BigDecimal("300"));
        assertMoney("500", bank.getTotalDeposited(checking.getAccountNumber()));
    }

    @Test
    void ownerCanOpenUpToTheLimit() {
        bank.openSavingsAccount("Sabo", new BigDecimal("500"));
        bank.openCheckingAccount("Sabo", new BigDecimal("500"));

        assertEquals(4, bank.getAllAccounts().size());
    }

    @Test
    void openingBeyondTheLimitIsRejected() {
        bank.openSavingsAccount("Sabo", new BigDecimal("500"));
        bank.openCheckingAccount("Sabo", new BigDecimal("500"));

        TooManyAccountsException e = assertThrows(TooManyAccountsException.class,
                () -> bank.openCheckingAccount("Sabo", new BigDecimal("500")));

        assertEquals("Sabo already has a maximum of 3 accounts", e.getMessage());
    }

    @Test
    void rejectedAccountIsNotStored() {
        bank.openSavingsAccount("Sabo", new BigDecimal("500"));
        bank.openCheckingAccount("Sabo", new BigDecimal("500"));

        assertThrows(TooManyAccountsException.class,
                () -> bank.openCheckingAccount("Sabo", new BigDecimal("500")));

        assertEquals(4, bank.getAllAccounts().size());
    }

    @Test
    void accountsByTypeGroupsSavingsAndChecking() {
        Map<String, List<Account>> byType = bank.getAccountsByType();
        assertEquals(List.of(savings), byType.get("SAVINGS"));
        assertEquals(List.of(checking), byType.get("CHECKING"));
        assertEquals(2, byType.size());
    }

    private static void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "expected " + expected + " but was " + actual);
    }
}