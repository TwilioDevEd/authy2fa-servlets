package com.twilio.authy2fa.servlet.authentication;

import com.twilio.authy2fa.lib.SessionManager;
import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
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

        User user = service.findByCredentials(email, password);
        if (user != null) {
            sessionManager.LogIn(request, user.getId());
            response.sendRedirect("/welcome");
        } else {
            request.setAttribute("data", "Your credentials are incorrect");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
