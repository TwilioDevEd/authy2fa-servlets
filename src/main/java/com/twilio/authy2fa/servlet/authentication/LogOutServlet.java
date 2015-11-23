package com.twilio.authy2fa.servlet.authentication;

import com.twilio.authy2fa.lib.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = {"/logout"})
public class LogOutServlet extends HttpServlet {

    private static final SessionManager SessionManager = new SessionManager();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        SessionManager.logOut(request);
        response.sendRedirect("login.jsp");
    }
}
