package com.twilio.authy2fa.servlet.requestvalidation;

public class RequestValidationResult {

    private String status;
    private Boolean validSignature;
    private String authyId;

    public RequestValidationResult(String status, Boolean validSignature) {
        this.status = status;
        this.validSignature = validSignature;
    }

    public RequestValidationResult(String status, Boolean validSignature, String authyId) {
        this.status = status;
        this.authyId = authyId;
        this.validSignature = validSignature;
    }

    public Boolean isValidSignature() {
        return validSignature;
    }

    public String getAuthyId() {
        return authyId;
    }


    public String getStatus() {
        return status;
    }
}