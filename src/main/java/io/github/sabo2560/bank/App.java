package io.github.sabo2560.bank;
import io.github.sabo2560.bank.exception.InsufficientFundsException;

public class App {
    public static void main(String[] args) {
        System.out.println(new InsufficientFundsException("ACC-1001", 200, 150).getMessage());
    }
}
