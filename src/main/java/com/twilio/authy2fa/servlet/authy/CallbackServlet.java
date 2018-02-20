package com.twilio.authy2fa.servlet.authy;

import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;
import com.twilio.authy2fa.servlet.requestvalidation.AuthyRequestValidator;
import com.twilio.authy2fa.servlet.requestvalidation.RequestValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/authy/callback"})
public class CallbackServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackServlet.class);

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

        AuthyRequestValidator validator = new AuthyRequestValidator(
                System.getenv("AUTHY_API_KEY"), request);
        RequestValidationResult validationResult = validator.validate();

        if (validationResult.isValidSignature()) {
            // Handle approved, denied, unauthorized
            User user = userService.findByAuthyId(validationResult.getAuthyId());
            user.setAuthyStatus(validationResult.getStatus());

            userService.update(user);
        } else {
            LOGGER.error("Received Authy callback but couldn't verify the signature");
        }
    }
}
