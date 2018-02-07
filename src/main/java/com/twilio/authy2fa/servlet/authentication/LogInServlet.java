package com.twilio.authy2fa.servlet.authentication;

import com.authy.AuthyApiClient;
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

@WebServlet(urlPatterns = {"/login"})
public class LogInServlet extends HttpServlet {

    private static final Logger  LOGGER = LoggerFactory.getLogger(LogInServlet.class);

    private final SessionManager sessionManager;
    private final UserService userService;
    private final AuthyApiClient client;
    private final ApprovalRequestService approvalRequestService;

    @SuppressWarnings("unused")
    public LogInServlet() {
        this(
                new SessionManager(),
                new UserService(),
                new AuthyApiClient(System.getenv("AUTHY_API_KEY")),
                new ApprovalRequestService());
    }

    public LogInServlet(SessionManager sessionManager, UserService userService,
                        AuthyApiClient client, ApprovalRequestService approvalRequestService) {
        this.sessionManager = sessionManager;
        this.userService = userService;
        this.client = client;
        this.approvalRequestService = approvalRequestService;
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
                String verificationStrategy = approvalRequestService.sendApprovalRequest(user,
                        client);
                response.getOutputStream().write(verificationStrategy.getBytes());
            } catch (ApprovalRequestException e) {
                LOGGER.error(e.getMessage());
                response.getOutputStream().write("unexpectedError".getBytes());
            }
        } else {
            response.getOutputStream().write("unauthorized".getBytes());
        }
    }


}
