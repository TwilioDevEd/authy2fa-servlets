package com.twilio.authy2fa.servlet.session;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = {"/login"})
public class LogInServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String storedPassword = "secret";
    private static final int MAX_INACTIVE_INTERVAL = 30 * 60;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String password = request.getParameter("password");
        if (password.equals(storedPassword)) {
            HttpSession session = request.getSession();
            session.setAttribute("authenticated", true);
            session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);

            response.sendRedirect("/welcome");
        } else {
            request.setAttribute("data", "Your credentials are incorrect");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
