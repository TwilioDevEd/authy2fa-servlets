package com.authy.onetouch;

import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class RequestValidator {

    private final String authyApiKey;
    private final HttpServletRequest request;

    /**
     * Request Validator initializer
     *
     * @param authyApiKey   The Authy API key used to access the rest API
     * @param request       The request
     */
    public RequestValidator(String authyApiKey, HttpServletRequest request) {
        this.authyApiKey = authyApiKey;
        this.request = request;
    }

    public boolean validate() throws IOException {

        String requestContent = getRequestContent(this.request.getReader());

        JSONObject content = new JSONObject(requestContent);

        Map<String, Object> parsedContent = parse(content);
        String sortedParams = serialize(parsedContent);


        String nonce = this.request.getHeader("X-Authy-Signature-Nonce");
        String method = this.request.getMethod();
        String url = this.request.getRequestURL().toString();
        String data = String.format("%s|%s|%s|%s", nonce, method, url, sortedParams);

        String computedDigest;
        try {
            computedDigest = computeDigest(data, this.authyApiKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }

        String authySignature = this.request.getHeader("X-Authy-Signature");
        return computedDigest.equals(authySignature);
    }

    protected String getRequestContent(BufferedReader bufferedReader) throws IOException {

        StringBuilder result = new StringBuilder();
        String line;
        while((line = bufferedReader.readLine()) != null){
            result.append(line);
        }

        return result.toString();
    }

    private Map<String, Object> parse(JSONObject content) {
        Map<String, Object> result = new HashMap<>();

        result.put("device_uuid", content.get("device_uuid"));
        result.put("callback_action", content.get("callback_action"));
        result.put("uuid", content.get("uuid"));
        result.put("device_uuid", content.get("device_uuid"));
        result.put("status", content.get("status"));
        result.put("signature", content.get("signature"));
        result.put("authy_id", content.get("authy_id"));

        JSONObject approvalRequest = content.getJSONObject("approval_request");
        result.put("approval_request[logos]", approvalRequest.get("logos"));
        result.put("approval_request[expiration_timestamp]", approvalRequest.get("expiration_timestamp"));

        JSONObject transaction = approvalRequest.getJSONObject("transaction");
        result.put("approval_request[transaction][device_details]", transaction.get("device_details"));
        result.put("approval_request[transaction][device_geolocation]", transaction.get("device_geolocation"));
        result.put("approval_request[transaction][device_signing_time]", transaction.get("device_signing_time"));
        result.put("approval_request[transaction][encrypted]", transaction.get("encrypted"));
        result.put("approval_request[transaction][flagged]", transaction.get("flagged"));
        result.put("approval_request[transaction][message]", transaction.get("message"));
        result.put("approval_request[transaction][reason]", transaction.get("reason"));
        result.put("approval_request[transaction][requester_details]", transaction.get("requester_details"));
        result.put("approval_request[transaction][status]", transaction.get("status"));
        result.put("approval_request[transaction][uuid]", transaction.get("uuid"));
        result.put("approval_request[transaction][created_at_time]", transaction.get("created_at_time"));
        result.put("approval_request[transaction][customer_uuid]", transaction.get("customer_uuid"));

        JSONObject details = transaction.getJSONObject("details");
        Set<String> detailsKeys = details.keySet();
        if (detailsKeys.size() > 0) {
            for (String key : detailsKeys) {
                result.put(String.format("approval_request[transaction][details][%s]", key), details.get(key));
            }
        }

        JSONObject hiddenDetails = transaction.getJSONObject("hidden_details");
        Set<String> hiddenDetailsKeys = hiddenDetails.keySet();
        if (hiddenDetailsKeys.size() > 0) {
            for (String key : hiddenDetailsKeys) {
                result.put(String.format("approval_request[transaction][hidden_details][%s]", key), hiddenDetails.get(key));
            }
        }

        return result;
    }

    public String serialize(Map<String, Object> map) {
        List<String> result = new ArrayList<>();
        SortedSet<String> keys = new TreeSet<>(map.keySet());
        for (String key : keys) {
            String content = String.format("%s=%s", encode(key), encode(map.get(key).toString()));
            result.add(content);
        }

        return String.join("&", result);
    }

    public String encode(String content) {

        return content
                .replace("null", "")
                .replace("[", "%5B")
                .replace("]", "%5D")
                .replace("@", "%40")
                .replace("=", "%3D")
                .replace("/", "%2F")
                .replace("+", "%2B")
                .replace(" ", "+");
    }

    private String computeDigest(String message, String secret) throws NoSuchAlgorithmException, InvalidKeyException {

        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256HMAC.init(secretKey);

        byte[] hashedMessage = sha256HMAC.doFinal(message.getBytes());

        return org.apache.commons.codec.binary.Base64.encodeBase64String(hashedMessage);
    }
}
