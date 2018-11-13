package com.twilio.authy2fa.servlet.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.authy2fa.exception.AuthyRequestException;
import com.twilio.authy2fa.lib.SessionManager;
import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;
import com.twilio.authy2fa.service.AuthyRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.twilio.authy2fa.servlet.authentication.LoginResponse.*;

@WebServlet(urlPatterns = {"/login"})
public class LogInServlet extends HttpServlet {

    private static final Logger  LOGGER = LoggerFactory.getLogger(LogInServlet.class);

    private final SessionManager sessionManager;
    private final UserService userService;
    private final AuthyRequestService authyRequestService;
    private final ObjectMapper objectMapper;

    @SuppressWarnings("unused")
    public LogInServlet() {
        this(
                new SessionManager(),
                new UserService(),
                new AuthyRequestService(),
                new ObjectMapper());
    }

    public LogInServlet(SessionManager sessionManager, UserService userService,
                        AuthyRequestService authyRequestService, ObjectMapper objectMapper) {
        this.sessionManager = sessionManager;
        this.userService = userService;
        this.authyRequestService = authyRequestService;
        this.objectMapper = objectMapper;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = userService.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            sessionManager.partialLogIn(request, user.getId());
            user.setAuthyStatus("");
            userService.update(user);
            try {
                String verificationStrategy = authyRequestService
                        .sendApprovalRequest(user);
                LoginResult loginResult =
                        LoginResult.valueOf(verificationStrategy.toUpperCase());
                response.getOutputStream().write(getResponseBytes(loginResult));
            } catch (AuthyRequestException e) {
                LOGGER.error(e.getMessage());
                response.getOutputStream().write(
                        getResponseBytes(LoginResult.ERROR, e.getMessage()));
            }
        } else {
            response.getOutputStream().write(
                    getResponseBytes(LoginResult.ERROR, "Invalid Credentials"));
        }
    }

    private byte[] getResponseBytes(LoginResult loginResult)
            throws JsonProcessingException {
        return getResponseBytes(loginResult, null);
    }

    private byte[] getResponseBytes(LoginResult result, String message)
            throws JsonProcessingException {
        LoginResponse loginResponse = new LoginResponse(
                result, message);
        return objectMapper.writeValueAsString(loginResponse).getBytes();
    }
}
