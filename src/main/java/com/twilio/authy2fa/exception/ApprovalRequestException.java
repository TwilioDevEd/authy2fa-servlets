package com.twilio.authy2fa.exception;

public class ApprovalRequestException extends RuntimeException {
    public ApprovalRequestException(String message) {
        super(message);
    }
}