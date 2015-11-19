package com.authy.onetouch;

import com.authy.onetouch.approvalrequest.Parameters;
import com.authy.onetouch.approvalrequest.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.List;

/**
 * A Java wrapper for the OneTouch REST API as specified in
 * <a href="https://docs.authy.com/onetouch.html">Authy Docs</a>
 *
 * @author Agustin Camino
 */
public class Client {

    // TODO: Add sandbox support.
    public static final String BASE_URL = "https://api.authy.com";

    private final String authyApiKey;
    private final String userId;

    /**
     * One Touch Client initializer.
     * @param authyApiKey The Authy API key used to access the rest API
     * @param userId The user id used to send approval request
     */
    public Client(String authyApiKey, String userId) {
        this.authyApiKey = authyApiKey;
        this.userId = userId;
    }

    /**
     * Send an approval request to Authy
     * @param message The message to be displayed in the device
     * @throws IOException
     */
    public Result sendApprovalRequest(String message, Parameters parameters) throws IOException {

        String url = String.format("%s/onetouch/json/users/%s/approval_requests?api_key=%s&message=%s",
                BASE_URL, userId, authyApiKey, URLEncoder.encode(message, "UTF-8"));


        HttpResponse response = sendPost(url, parameters.getParameters());

        // TODO: Handle the return code: response.getStatusLine().getStatusCode();
        // SC_OK 200
        // SC_BAD_REQUEST 400
        // SC_UNAUTHORIZED 401
        // SC_NOT_FOUND 404
        // SC_SERVICE_UNAVAILABLE 503

        String rawResponse = readContent(response);
        return fromJsonToResult(rawResponse, Result.class);
    }

    // TODO: Encapsulate this method into a WebClient
    private HttpResponse sendPost(String url, List<NameValuePair> parameters) throws IOException {

        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        post.setEntity(new UrlEncodedFormEntity(parameters));

        HttpResponse response = client.execute(post);
        return response;
    }

    private String readContent(HttpResponse response) throws IOException {
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }

    private <T> T fromJsonToResult(String json, Class<T> klass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, klass);
    }
}