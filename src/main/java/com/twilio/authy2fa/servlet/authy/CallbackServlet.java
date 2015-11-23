package com.twilio.authy2fa.servlet.authy;

import com.authy.onetouch.RequestValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/authy/callback"})
public class CallbackServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RequestValidator validator = new RequestValidator(System.getenv("AUTHY_API_KEY"), request);
        if (validator.validate()) {
            // Complete authentication
        }
    }
}
