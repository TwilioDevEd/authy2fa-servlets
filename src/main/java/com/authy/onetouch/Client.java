package com.authy.onetouch;

import com.authy.lib.ContentReader;
import com.authy.lib.HttpClient;
import com.authy.lib.Mapper;
import com.authy.onetouch.approvalrequest.Parameters;
import com.authy.onetouch.approvalrequest.Result;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * A Java wrapper for the OneTouch REST API as specified in
 * <a href="https://docs.authy.com/onetouch.html">Authy Docs</a>.
 *
 * @author Agustin Camino
 */
public class Client extends HttpClient {

    // TODO: Add sandbox support.
    public static final String BASE_URL = "https://api.authy.com";

    private final String authyApiKey;

    /**
     * One Touch Client initializer.
     *
     * @param authyApiKey   The Authy API key used to access the rest API
     */
    public Client(String authyApiKey) {

        this.authyApiKey = authyApiKey;
    }

    /**
     * Send an approval request to Authy.
     *
     * @param message   The message to be displayed in the device
     * @param userId        The user id used to send approval request
     *
     * @throws IOException
     */
    public Result sendApprovalRequest(String userId, String message, Parameters parameters) throws IOException {

        String url = String.format("%s/onetouch/json/users/%s/approval_requests?api_key=%s&message=%s",
                BASE_URL, userId, authyApiKey, URLEncoder.encode(message, "UTF-8"));

        HttpResponse response = sendPost(url, parameters.getParams());

        // TODO: Handle the return code: response.getStatusLine().getStatusCode();
        // SC_OK 200
        // SC_BAD_REQUEST 400
        // SC_UNAUTHORIZED 401
        // SC_NOT_FOUND 404
        // SC_SERVICE_UNAVAILABLE 503

        String content = new ContentReader().read(response);
        return new Mapper().fromJsonToObject(content, Result.class);
    }
}