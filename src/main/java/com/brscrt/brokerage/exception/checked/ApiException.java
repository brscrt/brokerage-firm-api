package com.brscrt.brokerage.exception.checked;

import java.util.Objects;

public class ApiException extends Exception {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        String message = getClass().getSimpleName() + ": " + getMessage();
        if (Objects.nonNull(getCause())) {
            return message + " Cause: " + getCause().getMessage();
        } else {
            return message;
        }
    }
}