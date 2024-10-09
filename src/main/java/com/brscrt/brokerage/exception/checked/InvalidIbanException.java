package com.brscrt.brokerage.exception.checked;

public class InvalidIbanException extends ApiException {

    public InvalidIbanException(String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public InvalidIbanException(Throwable cause) {
        super(cause);
    }

    @SuppressWarnings("unused")
    public InvalidIbanException(String message, Throwable cause) {
        super(message, cause);
    }
}