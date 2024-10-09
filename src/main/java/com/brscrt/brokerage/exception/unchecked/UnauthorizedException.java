package com.brscrt.brokerage.exception.unchecked;

public class UnauthorizedException extends ApiRuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public UnauthorizedException(Throwable cause) {
        super(cause);
    }

    @SuppressWarnings("unused")
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}