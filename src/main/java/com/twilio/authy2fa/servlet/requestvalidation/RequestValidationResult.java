package com.twilio.authy2fa.servlet.requestvalidation;

public class RequestValidationResult {

    private String status;
    private Boolean validSignature;
    private int authyId;

    public RequestValidationResult(String status, Boolean validSignature) {
        this.status = status;
        this.validSignature = validSignature;
    }

    public RequestValidationResult(String status, Boolean validSignature, int authyId) {
        this.status = status;
        this.authyId = authyId;
        this.validSignature = validSignature;
    }

    public Boolean isValidSignature() {
        return validSignature;
    }

    public int getAuthyId() {
        return authyId;
    }


    public String getStatus() {
        return status;
    }
}