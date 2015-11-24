package com.authy.onetouch;

import com.authy.lib.ContentReader;
import com.authy.lib.JSONEncoder;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class RequestValidator {

    private final String authyApiKey;
    private final HttpServletRequest request;

    public final static String UNAUTHORIZED = "unauthorized";

    /**
     * Request Validator initializer.
     *
     * @param authyApiKey   The Authy API key used to access the rest API
     * @param request       The request
     */
    public RequestValidator(String authyApiKey, HttpServletRequest request) {
        this.authyApiKey = authyApiKey;
        this.request = request;
    }

    public String validate() throws IOException {

        String requestContent = new ContentReader().read(this.request);
        JSONObject content = new JSONObject(requestContent);
        String sortedParams = JSONEncoder.encode(content);

        String nonce = this.request.getHeader("X-Authy-Signature-Nonce");
        String method = this.request.getMethod();
        String url = this.request.getRequestURL().toString();
        String data = String.format("%s|%s|%s|%s", nonce, method, url, sortedParams);

        String computedDigest;
        try {
            computedDigest = computeDigest(data, this.authyApiKey);
        } catch (Exception e) {
            e.printStackTrace();
            return UNAUTHORIZED;
        }

        String authySignature = this.request.getHeader("X-Authy-Signature");
        return computedDigest.equals(authySignature) ? content.getString("status") : UNAUTHORIZED;
    }

    private String computeDigest(String message, String secret) throws NoSuchAlgorithmException, InvalidKeyException {

        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256HMAC.init(secretKey);

        byte[] hashedMessage = sha256HMAC.doFinal(message.getBytes());

        return org.apache.commons.codec.binary.Base64.encodeBase64String(hashedMessage);
    }
}
