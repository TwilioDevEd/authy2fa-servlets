package com.twilio.authy2fa.service;

import com.authy.AuthyApiClient;
import com.authy.OneTouchException;
import com.authy.api.ApprovalRequestParams;
import com.authy.api.Hash;
import com.authy.api.OneTouchResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.authy2fa.exception.ApprovalRequestException;
import com.twilio.authy2fa.models.User;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ApprovalRequestService {

    private static final String AUTHY_USERS_URI_TEMPLATE = "%s/protected/json/users/%s/status?api_key=%s";
    private final String authyBaseURL;

    private final ConfigurationService configuration;

    public ApprovalRequestService() {
        this.authyBaseURL =  "https://api.authy.com";
        this.configuration = new ConfigurationService();
    }

    public ApprovalRequestService(String authyBaseURL, ConfigurationService configuration) {
        this.authyBaseURL = authyBaseURL;
        this.configuration = configuration;
    }

    public String sendApprovalRequest(User user, AuthyApiClient client) {
        if(hasAuthyApp(user)){
            try {
                OneTouchResponse result = sendOneTouchApprovalRequest(user, client);
                if(!result.isSuccess()) {
                    throw new ApprovalRequestException(result.getMessage());
                }
                return "onetouch";
            } catch (IOException | OneTouchException e) {
                throw new ApprovalRequestException(e.getMessage());
            }
        } else {
            Hash result = sendSMSToken(user, client);
            if(!result.isSuccess()) {
                throw new ApprovalRequestException(result.getMessage());
            }
            return "sms";
        }
    }

    private boolean hasAuthyApp(User user) {
        ObjectMapper objectMapper = new ObjectMapper();
        String url = String.format(AUTHY_USERS_URI_TEMPLATE,
                authyBaseURL,
                user.getAuthyId(),
                configuration.authyApiKey()
        );
        try {
            String responseBody = Request.Get(url)
                    .connectTimeout(10000)
                    .socketTimeout(10000)
                    .execute().returnContent().asString();
            TypeReference<HashMap<String,Object>> typeRef
                    = new TypeReference<HashMap<String,Object>>() {};
            HashMap<String,HashMap> o = objectMapper.readValue(responseBody, typeRef);

            return (Boolean) ((Map<String, Object>)o.get("status")).get("registered");
        } catch (IOException e) {
            throw new ApprovalRequestException(e.getMessage());
        }
    }

    private OneTouchResponse sendOneTouchApprovalRequest(User user, AuthyApiClient client)
            throws IOException, OneTouchException {
        ApprovalRequestParams parameters = new ApprovalRequestParams.Builder(
                Integer.valueOf(user.getAuthyId()),
                "Request login to Twilio demo app")
                .addDetail("email", user.getEmail())
                .build();

        return client.getOneTouch().sendApprovalRequest(parameters);
    }

    private Hash sendSMSToken(User user, AuthyApiClient client){
        return client.getUsers().requestSms(Integer.valueOf(user.getAuthyId()));
    }
} 