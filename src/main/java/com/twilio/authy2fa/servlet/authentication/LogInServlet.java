package com.twilio.authy2fa.servlet.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.authy2fa.exception.ApprovalRequestException;
import com.twilio.authy2fa.lib.SessionManager;
import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;
import com.twilio.authy2fa.service.ApprovalRequestService;
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
    private final ApprovalRequestService approvalRequestService;
    private final ObjectMapper objectMapper;

    @SuppressWarnings("unused")
    public LogInServlet() {
        this(
                new SessionManager(),
                new UserService(),
                new ApprovalRequestService(),
                new ObjectMapper());
    }

    public LogInServlet(SessionManager sessionManager, UserService userService,
                        ApprovalRequestService approvalRequestService, ObjectMapper objectMapper) {
        this.sessionManager = sessionManager;
        this.userService = userService;
        this.approvalRequestService = approvalRequestService;
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
            try {
                String verificationStrategy = approvalRequestService
                        .sendApprovalRequest(user);
                LoginResult loginResult =
                        LoginResult.valueOf(verificationStrategy.toUpperCase());
                response.getOutputStream().write(getResponseBytes(loginResult));
            } catch (ApprovalRequestException e) {
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
