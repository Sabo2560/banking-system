package io.github.sabo2560.bank.exception;

public class TooManyAccountsException extends RuntimeException {
    private final String ownerName;
    private final int limit;
    public TooManyAccountsException(String ownerName, int limit) {
        super(String.format("%s already has a maximum of %d accounts",ownerName, limit));

        this.ownerName = ownerName;
        this.limit=limit;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public int getLimit() {
        return limit;
    }
}
