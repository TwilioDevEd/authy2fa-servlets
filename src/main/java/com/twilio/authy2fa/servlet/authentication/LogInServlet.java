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
    private static final long serialVersionUID = 1L;

    private static final SessionManager sessionManager = new SessionManager();
    private static final UserService service = new UserService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = service.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            sessionManager.LogIn(request, user.getId());
            sendApprovalRequest(user);

            response.sendRedirect("/welcome");
        } else {
            request.setAttribute("data", "Your credentials are incorrect");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void sendApprovalRequest(User user) throws IOException {
        Client client = new Client(System.getenv("AUTHY_API_KEY"), user.getAuthyId());
        Parameters parameters = Parameters.builder()
                .addDetail("email", user.getEmail())
                .build();

        client.sendApprovalRequest("Request login to Twilio demo app", parameters);
    }
}
