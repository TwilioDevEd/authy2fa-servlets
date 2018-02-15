package com.twilio.authy2fa.servlet.authentication;

public class LoginResponse {

    enum LoginResult{
        SMS, ONETOUCH, ERROR
    }

    private LoginResult result;

    private String message;

    public LoginResponse(LoginResult result, String message) {
        this.result = result;
        this.message = message;
    }

    public LoginResult getResult() {
        return result;
    }

    public void setResult(LoginResult result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LoginResponse() {

    }
}
