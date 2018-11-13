package com.twilio.authy2fa.servlet.requestvalidation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.net.URLEncoder.encode;

public class AuthyRequestValidator {

    private final String authyApiKey;
    private final HttpServletRequest request;

    public final static String UNAUTHORIZED = "unauthorized";
    private final ObjectMapper objectMapper;

    /**
     * Request Validator initializer.
     *
     * @param authyApiKey   The Authy API key used to access the rest API
     * @param request       The request
     */
    public AuthyRequestValidator(String authyApiKey, HttpServletRequest request) {
        this.authyApiKey = authyApiKey;
        this.request = request;
        this.objectMapper = new ObjectMapper();
    }


    public RequestValidationResult validate() throws IOException {
        String forwardedProtocol = request.getHeader("X-Forwarded-Proto");
        String protocol = forwardedProtocol != null ? forwardedProtocol : request.getScheme();
        String url = String.format("%s://%s%s", protocol,
                request.getServerName(), request.getServletPath());

        // Fetch Json body
        String body = IOUtils.toString(request.getReader());
        Map<String, Object> bodyJson = objectMapper.readValue(body,
                new TypeReference<Map<String, Object>>() {
        });

        // Flatten and sort JSON elements
        String flattenJson = bodyJson.entrySet()
                .stream()
                .flatMap(FlatMap::flatten)
                .map(e -> e.getKey() + "=" + e.getValue())
                .sorted()
                .collect(Collectors.joining("&"));

        // Read the nonce from the request
        String nonce = request.getHeader("X-Authy-Signature-Nonce");

        // Join all the request bits using '|'
        String data = String.format("%s|%s|%s|%s",
                nonce,
                request.getMethod(),
                url,
                flattenJson);
        Boolean isValidSignature = false;

        try {
            // Compute the signature
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(authyApiKey.getBytes(),
                    "HmacSHA256");
            sha256_HMAC.init(secret_key);
            String hash = Base64
                    .getEncoder()
                    .encodeToString(sha256_HMAC.doFinal(data.getBytes()));

            // Extract the actual request signature
            String signature = request.getHeader("X-Authy-Signature");

            // Compute the request signature with your computed signature
            isValidSignature = hash.equals(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        String status = isValidSignature ? bodyJson.get("status").toString() : UNAUTHORIZED;
        int authyId = Integer.parseInt(bodyJson.get("authy_id").toString());
        RequestValidationResult result = new RequestValidationResult(status, isValidSignature, authyId);

        return result;
    }
}

class FlatMap {

    private static String openBracket;
    private static String closeBracket;

    static {
        try {
            openBracket = encode("[", "UTF-8");
            closeBracket = encode("]", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String prependKeyName(String keyName, String prefix) {
        StringBuffer result = new StringBuffer();
        result.append(prefix);
        int indexOfOpenBracket = keyName.indexOf(openBracket);
        if(indexOfOpenBracket == -1) {
            indexOfOpenBracket = keyName.length();
        }
        result.append(openBracket);
        result.append(keyName.substring(0, indexOfOpenBracket));
        result.append(closeBracket);
        result.append(keyName.substring(indexOfOpenBracket));
        return result.toString();

    }

    public static Stream<Map.Entry<?, ?>> flatten(Map.Entry<?, ?> e) {
        Object value = e.getValue();
        if (value instanceof Map<?, ?>) {
            return ((Map<?, ?>) value).entrySet()
                    .stream()
                    .flatMap(FlatMap::flatten)
                    .map(el -> {
                        String newKeyName = FlatMap.prependKeyName(
                                (String) el.getKey(), (String) e.getKey());
                        return new AbstractMap.SimpleEntry<>(newKeyName, el.getValue());
                    });
        } else if(value == null) {
            return Stream.of(new AbstractMap.SimpleEntry<>(e.getKey(), ""));
        } else if(value instanceof String) {
            try {
                String encodedValue = URLEncoder.encode((String) value, "UTF-8");
                String encodedKey = URLEncoder.encode((String) e.getKey(), "UTF-8");
                return Stream.of(new AbstractMap.SimpleEntry<>(
                        encodedKey.replace(" ", "+"),
                        encodedValue.replace(" ", "+")
                ));
            } catch (UnsupportedEncodingException exception) {
                throw new RuntimeException(exception);
            }
        }
        return Stream.of(e);
    }
}

