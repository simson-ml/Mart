package com.simson.shopsphere.exception;

public class ChatProviderException extends RuntimeException {

    public ChatProviderException(String message) {
        super(message);
    }

    public ChatProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
