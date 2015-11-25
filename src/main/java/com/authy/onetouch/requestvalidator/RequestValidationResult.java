package com.authy.onetouch.requestvalidator;

public class RequestValidationResult {

    private String status;
    private String authyId;

    public RequestValidationResult(String status) {
        this.status = status;
    }

    public RequestValidationResult(String status, String authyId) {
        this.status = status;
        this.authyId = authyId;
    }

    public String getStatus() {
        return status;
    }

    public String getAuthyId() {
        return authyId;
    }

    public boolean isValid() {
        return status.equals("approved") || status.equals("denied");
    }
}
