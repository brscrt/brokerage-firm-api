package com.brscrt.brokerage.exception.checked;

public class InsufficientFundsException extends ApiException {

    public InsufficientFundsException(String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public InsufficientFundsException(Throwable cause) {
        super(cause);
    }

    @SuppressWarnings("unused")
    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }
}