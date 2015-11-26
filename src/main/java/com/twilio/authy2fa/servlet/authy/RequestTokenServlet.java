package com.twilio.authy2fa.servlet.authy;

import com.authy.AuthyApiClient;
import com.authy.api.Hash;
import com.twilio.authy2fa.lib.SessionManager;
import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/authy/request-token"})
public class RequestTokenServlet extends HttpServlet {

    private final SessionManager sessionManager;
    private final UserService userService;
    private final AuthyApiClient authyClient;

    @SuppressWarnings("unused")
    public RequestTokenServlet() {
        this(
                new SessionManager(),
                new UserService(),
                new AuthyApiClient(System.getenv("AUTHY_API_KEY"))
        );
    }

    public RequestTokenServlet(SessionManager sessionManager, UserService userService, AuthyApiClient authyClient) {
        this.sessionManager = sessionManager;
        this.userService = userService;
        this.authyClient = authyClient;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long userId = sessionManager.getLoggedUserId(request);
        User user = userService.find(userId);

        Hash result = authyClient.getUsers().requestSms(Integer.parseInt(user.getAuthyId()));
        String verificationStrategy =
                result.getMessage().contains("Ignored") ? "soft-token" : "one-code";

        response.getOutputStream().write(verificationStrategy.getBytes());
    }
}
