package io.github.sabo2560.bank.model;

import io.github.sabo2560.bank.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckingAccountTest {

    @Test
    void blankOwnerNameIsRejected() {

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new CheckingAccount("  ", 1000));

        assertEquals("Owner name must be filled!", e.getMessage());
    }

    @Test
    void withdrawingIntoOverdraftSucceeds() throws InsufficientFundsException {
        CheckingAccount acc = new CheckingAccount("Sabotest", 200);
        acc.withdraw(700);
        assertEquals(-500.00, acc.getBalance(), 0.001);
    }

    @Test
    void withdrawingBeyondOverdraftIsRejected() throws InsufficientFundsException {
        CheckingAccount acc = new CheckingAccount("Sabotest", 200);
        acc.withdraw(700);
        assertEquals(-500.00, acc.getBalance(), 0.001);
        InsufficientFundsException e = assertThrows(InsufficientFundsException.class,
                () -> acc.withdraw(0.01));
        assertEquals(0.00, e.getAvailable(), 0.001);
        assertEquals(-500, acc.getBalance(), 0.001);
    }
}