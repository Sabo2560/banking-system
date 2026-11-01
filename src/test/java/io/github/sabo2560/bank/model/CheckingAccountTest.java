package io.github.sabo2560.bank.model;

import io.github.sabo2560.bank.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CheckingAccountTest {

    @Test
    void blankOwnerNameIsRejected() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new CheckingAccount("  ", new BigDecimal("1000")));

        assertEquals("Owner name must be filled!", e.getMessage());
    }

    @Test
    void withdrawingIntoOverdraftSucceeds() throws InsufficientFundsException {
        CheckingAccount account = new CheckingAccount("Sabo", new BigDecimal("200"));

        account.withdraw(new BigDecimal("700"));

        assertMoney("-500", account.getBalance());
    }

    @Test
    void withdrawingBeyondOverdraftIsRejected() throws InsufficientFundsException {
        CheckingAccount account = new CheckingAccount("Sabo", new BigDecimal("200"));
        account.withdraw(new BigDecimal("700"));
        assertMoney("-500", account.getBalance());

        InsufficientFundsException e = assertThrows(InsufficientFundsException.class,
                () -> account.withdraw(new BigDecimal("0.01")));

        assertMoney("0", e.getAvailable());
        assertMoney("-500", account.getBalance());
    }

    private static void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "expected " + expected + " but was " + actual);
    }
}