package com.twilio.authy2fa.servlet.authy;

import com.twilio.authy2fa.lib.SessionManager;
import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/authy/status"})
public class OneTouchStatusServlet extends HttpServlet {

    private final SessionManager sessionManager;
    private final UserService userService;

    @SuppressWarnings("unused")
    public OneTouchStatusServlet() {
        this(new SessionManager(), new UserService());
    }

    public OneTouchStatusServlet(SessionManager sessionManager, UserService userService) {
        this.sessionManager = sessionManager;
        this.userService = userService;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long userId = sessionManager.getLoggedUserId(request);
        User user = userService.find(userId);

        response.getOutputStream().write(user.getAuthyStatus().getBytes());
    }
}
