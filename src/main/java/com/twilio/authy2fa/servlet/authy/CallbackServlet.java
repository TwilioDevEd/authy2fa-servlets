package com.twilio.authy2fa.servlet.authy;

import com.authy.onetouch.RequestValidator;
import com.authy.onetouch.requestvalidator.RequestValidationResult;
import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/authy/callback"})
public class CallbackServlet extends HttpServlet {

    private static UserService userService;

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
            User user = this.userService.findByAuthyId(validationResult.getAuthyId());
            System.out.println(user);

            user.setAuthyStatus(validationResult.getStatus());

            this.userService.update(user);
        }
    }
}
