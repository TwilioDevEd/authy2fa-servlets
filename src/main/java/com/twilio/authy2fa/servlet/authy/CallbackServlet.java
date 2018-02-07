package com.twilio.authy2fa.servlet.authy;

import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;
import com.twilio.authy2fa.servlet.requestvalidation.RequestValidationResult;
import com.twilio.authy2fa.servlet.requestvalidation.RequestValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/authy/callback"})
public class CallbackServlet extends HttpServlet {

    private final UserService userService;

    @SuppressWarnings("unused")
    public CallbackServlet() {
        this(new UserService());
    }

    public CallbackServlet(UserService userService) {
        this.userService = userService;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RequestValidator validator = new RequestValidator(System.getenv("AUTHY_API_KEY"), request);
        RequestValidationResult validationResult = validator.validate();

        if (validationResult.isValid()) {

            // Handle approved, denied, unauthorized
            User user = userService.findByAuthyId(validationResult.getAuthyId());
            user.setAuthyStatus(validationResult.getStatus());

            userService.update(user);
        }
    }
}
