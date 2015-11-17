package com.twilio.authy2fa.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/account"})
public class AccountServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Retrieve this information from a database.
        request.setAttribute("name", "Bob");
        request.setAttribute("email", "bob@example.com");
        request.setAttribute("phoneNumber", "555-5555");
        request.getRequestDispatcher("/account.jsp").forward(request, response);
    }
}