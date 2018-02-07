package com.twilio.authy2fa.service;

public class ConfigurationService {

    public String authyApiKey() {
        return System.getenv("AUTHY_API_KEY");
    }
} 