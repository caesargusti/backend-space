package com.example.wallet.exception;

public class InsufficientFundsException extends WalletException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
