package com.twilio.authy2fa.servlet.authy;

import com.authy.onetouch.RequestValidator;
import com.twilio.authy2fa.lib.SessionManager;
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

    private static final SessionManager sessionManager = new SessionManager();
    private static final UserService service = new UserService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long userId = sessionManager.getLoggedUserId(request);
        User user = service.find(userId);

        RequestValidator validator = new RequestValidator(System.getenv("AUTHY_API_KEY"), request);
        user.setAuthyStatus(validator.validate());
        service.save(user);
    }
}
