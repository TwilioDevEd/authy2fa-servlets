package com.twilio.authy2fa.servlet.authentication;

import com.authy.onetouch.Client;
import com.authy.onetouch.approvalrequest.Parameters;
import com.twilio.authy2fa.lib.SessionManager;
import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/login"})
public class LogInServlet extends HttpServlet {

    private static SessionManager sessionManager;
    private static UserService userService;
    private static Client client;

    @SuppressWarnings("unused")
    public LogInServlet() {
        this(
                new SessionManager(),
                new UserService(),
                new Client(System.getenv("AUTHY_API_KEY"))
        );
    }

    public LogInServlet(SessionManager sessionManager, UserService userService, Client client) {
        this.sessionManager = sessionManager;
        this.userService = userService;
        this.client = client;
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
            sendApprovalRequest(user);
            response.getOutputStream().write("onetouch".getBytes());
        } else {
            request.setAttribute("data", "Your credentials are incorrect");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void sendApprovalRequest(User user) throws IOException {
        Parameters parameters = Parameters.builder()
                .addDetail("email", user.getEmail())
                .build();

        client.sendApprovalRequest(user.getAuthyId(), "Request login to Twilio demo app", parameters);
    }
}
