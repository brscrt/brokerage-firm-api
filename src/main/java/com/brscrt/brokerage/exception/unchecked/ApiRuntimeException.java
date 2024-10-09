package com.brscrt.brokerage.exception.unchecked;

import java.util.Objects;

public class ApiRuntimeException extends RuntimeException {

    public ApiRuntimeException(String message) {
        super(message);
    }

    public ApiRuntimeException(Throwable cause) {
        super(cause);
    }

    public ApiRuntimeException(String message, Throwable cause) {
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