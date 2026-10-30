package io.github.sabo2560.bank.model;

import io.github.sabo2560.bank.exception.InsufficientFundsException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {

    @Test
    void openingWithValidDepositCreatesAccountWithOneDeposit() {
        // Arrange + Act
        SavingsAccount account = new SavingsAccount("Sabo", 1000);

        // Assert
        assertEquals(1000.0, account.getBalance(), 0.001);
        assertEquals(1, account.getTransactions().size());
        assertEquals(TransactionType.DEPOSIT, account.getTransactions().get(0).getType());
        assertTrue(account.getAccountNumber().startsWith("ACC-"));
    }
    @Test
    void openingBelowMinimumIsRejected(){
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new SavingsAccount("SAVBA",50));
        assertEquals("Initial deposit must be at least 100.00",e.getMessage());
    }

    @Test
    void depositOfZeroOrNegativeIsRejected(){
        SavingsAccount acc = new SavingsAccount("SABSAV", 1000);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, ()-> acc.deposit( 0));
        assertEquals("Deposit amount must be greater than 0" , e.getMessage());
        assertEquals(1000,acc.getBalance(),0.001);

        e = assertThrows(IllegalArgumentException.class, () -> acc.deposit(-50));
        assertEquals("Deposit amount must be greater than 0" , e.getMessage());
        assertEquals(1000,acc.getBalance(),0.001);
    }

    @Test
    void withdrawingBelowMinimumBalanceIsRejected(){
        SavingsAccount acc = new SavingsAccount("Sabotest", 1000);
        InsufficientFundsException e = assertThrows(InsufficientFundsException.class, ()-> acc.withdraw(901));

        assertEquals(900.0, e.getAvailable(), 0.001);
        assertEquals(901.0, e.getRequested(), 0.001);
        assertEquals(1000,acc.getBalance(), 0.001);
    }

    @Test
    void withdrawingDownToMinimumBalanceSucceeds() throws InsufficientFundsException {
        SavingsAccount acc = new SavingsAccount("Sabotest", 1000);
        acc.withdraw(900);
        assertEquals(100.0,acc.getBalance(), 0.001);
    }
    @Test
    void applyingInterestAddsInterestTransaction() {

        SavingsAccount acc = new SavingsAccount("SaboTester",1000.00);

        acc.applyInterest();

        assertEquals(1040,acc.getBalance(),0.01);

         List<Transaction> transactions = acc.getTransactions();

        assertEquals(TransactionType.INTEREST,transactions.getLast().getType());

        assertEquals(40 , transactions.getLast().getAmount());
    }
}
