package com.twilio.authy2fa.exception;

public class AuthyRequestException extends RuntimeException {
    public AuthyRequestException(String message) {
        super(message);
    }
}