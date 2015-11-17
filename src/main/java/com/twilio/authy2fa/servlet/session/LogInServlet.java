package com.twilio.authy2fa.servlet.session;

import com.twilio.authy2fa.utils.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = {"/login"})
public class LogInServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String storedPassword = "secret";
    private static final SessionManager SessionManager = new SessionManager();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String password = request.getParameter("password");
        if (password.equals(storedPassword)) {
            SessionManager.LogIn(request);

            response.sendRedirect("/welcome");
        } else {
            request.setAttribute("data", "Your credentials are incorrect");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
