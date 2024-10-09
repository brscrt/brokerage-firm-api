package com.brscrt.brokerage.exception.checked;

public class InsufficientAssetException extends ApiException {

    public InsufficientAssetException(String message) {
        super(message);
    }

    @SuppressWarnings("unused")
    public InsufficientAssetException(Throwable cause) {
        super(cause);
    }

    @SuppressWarnings("unused")
    public InsufficientAssetException(String message, Throwable cause) {
        super(message, cause);
    }
}