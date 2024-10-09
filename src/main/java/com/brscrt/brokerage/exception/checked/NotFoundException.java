package com.brscrt.brokerage.exception.checked;

public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public NotFoundException(Throwable cause) {
        super(cause);
    }

    @SuppressWarnings("unused")
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}