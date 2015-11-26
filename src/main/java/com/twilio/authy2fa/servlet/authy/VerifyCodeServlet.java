package com.twilio.authy2fa.servlet.authy;

import com.authy.AuthyApiClient;
import com.authy.api.Token;
import com.twilio.authy2fa.lib.SessionManager;
import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/authy/verify-code"})
public class VerifyCodeServlet extends HttpServlet {

    private final SessionManager sessionManager;
    private final UserService userService;
    private final AuthyApiClient authyClient;

    @SuppressWarnings("unused")
    public VerifyCodeServlet() {
        this(
                new SessionManager(),
                new UserService(),
                new AuthyApiClient(System.getenv("AUTHY_API_KEY"))
        );
    }

    public VerifyCodeServlet(SessionManager sessionManager, UserService userService, AuthyApiClient authyClient) {
        this.sessionManager = sessionManager;
        this.userService = userService;
        this.authyClient = authyClient;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long userId = sessionManager.getLoggedUserId(request);
        User user = userService.find(userId);

        String authyToken = request.getParameter("authy-token");

        Token token = authyClient.getTokens().verify(Integer.parseInt(user.getAuthyId()), authyToken);
        if (token.isOk()) {
            sessionManager.logIn(request, user.getId());
            response.sendRedirect("/account");
        } else {
            sessionManager.logOut(request);
            request.setAttribute("data", "Token was invalid");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
