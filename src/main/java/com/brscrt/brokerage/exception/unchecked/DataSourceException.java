package com.brscrt.brokerage.exception.unchecked;

public class DataSourceException extends ApiRuntimeException {

    @SuppressWarnings("unused")
    public DataSourceException(String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public DataSourceException(Throwable cause) {
        super(cause);
    }

    public DataSourceException(String message, Throwable cause) {
        super(message, cause);
    }
}