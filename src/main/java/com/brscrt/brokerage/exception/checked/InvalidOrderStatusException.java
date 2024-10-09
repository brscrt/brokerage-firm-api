package com.brscrt.brokerage.exception.checked;

public class InvalidOrderStatusException extends ApiException {

    public InvalidOrderStatusException(String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public InvalidOrderStatusException(Throwable cause) {
        super(cause);
    }

    @SuppressWarnings("unused")
    public InvalidOrderStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}