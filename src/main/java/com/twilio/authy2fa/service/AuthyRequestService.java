package com.twilio.authy2fa.service;

import com.authy.AuthyApiClient;
import com.authy.AuthyException;
import com.authy.api.ApprovalRequestParams;
import com.authy.api.Hash;
import com.authy.api.OneTouchResponse;
import com.twilio.authy2fa.exception.AuthyRequestException;
import com.twilio.authy2fa.models.User;


public class AuthyRequestService {

    private final ConfigurationService configuration;
    private final AuthyApiClient client;

    public AuthyRequestService() {
        this.configuration = new ConfigurationService();
        this.client = new AuthyApiClient(configuration.authyApiKey());
    }

    public AuthyRequestService(ConfigurationService configuration, AuthyApiClient client) {
        this.configuration = configuration;
        this.client = client;
    }

    public String sendApprovalRequest(User user) {
        try {
            if(hasAuthyApp(user)){
                OneTouchResponse result = sendOneTouchApprovalRequest(user);
                if(!result.isSuccess()) {
                    throw new AuthyRequestException(result.getMessage());
                }
                return "onetouch";
            } else {
                Hash result = sendSMSToken(user);
                if(!result.isSuccess()) {
                    throw new AuthyRequestException(result.getError().getMessage());
                }
                return "sms";
            }
        } catch (AuthyException e) {
            throw new AuthyRequestException(e.getMessage());
        }
    }

    private boolean hasAuthyApp(User user) throws AuthyException {
        return client.getUsers().requestStatus(user.getAuthyId()).isRegistered();
    }

    private OneTouchResponse sendOneTouchApprovalRequest(User user)
            throws AuthyException {
        ApprovalRequestParams parameters = new ApprovalRequestParams.Builder(
                Integer.valueOf(user.getAuthyId()),
                "Request login to Twilio demo app")
                .addDetail("email", user.getEmail())
                .build();

        return client.getOneTouch().sendApprovalRequest(parameters);
    }

    private Hash sendSMSToken(User user) throws AuthyException {
        return client.getUsers().requestSms(Integer.valueOf(user.getAuthyId()));
    }

    public int sendRegistrationRequest(String email, String phoneNumber, String countryCode) throws AuthyRequestException {
        try {

            com.authy.api.User authyUser = client.getUsers().createUser(email, phoneNumber, countryCode);
            if (authyUser.isOk()) {
                return authyUser.getId();
            } else {
                throw new AuthyRequestException(authyUser.getError().getMessage());
            }

        } catch (AuthyException e) {
            throw new AuthyRequestException(e.getMessage());
        }
    }
} 