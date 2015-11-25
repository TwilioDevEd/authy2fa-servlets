package com.twilio.authy2fa.servlet;

import com.authy.AuthyApiClient;
import com.twilio.authy2fa.lib.RequestParametersValidator;
import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;
import com.twilio.authy2fa.lib.SessionManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/registration")
public class RegistrationServlet extends HttpServlet{

    private final SessionManager sessionManager;
    private final UserService userService;
    private final AuthyApiClient authyClient;

    @SuppressWarnings("unused")
    public RegistrationServlet() {
        this(
                new SessionManager(),
                new UserService(),
                new AuthyApiClient(System.getenv("AUTHY_API_KEY"))
        );
    }

    public RegistrationServlet(SessionManager sessionManager, UserService userService, AuthyApiClient authyClient) {
        this.sessionManager = sessionManager;
        this.userService = userService;
        this.authyClient = authyClient;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/registration.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String countryCode = request.getParameter("countryCode");
        String phoneNumber = request.getParameter("phoneNumber");

        if (validateRequest(request)) {

            User user = userService.create(new User(name, email, password, countryCode, phoneNumber));

            com.authy.api.User authyUser = authyClient.getUsers().createUser(email, phoneNumber, countryCode);

            if (authyUser.isOk()) {
                user.setAuthyId(Integer.toString(authyUser.getId()));
                userService.update(user);
            }

            sessionManager.logIn(request, user.getId());
            response.sendRedirect("/account");
        } else {
            preserveStatusRequest(request, name, email, countryCode, phoneNumber);
            request.getRequestDispatcher("/registration.jsp").forward(request, response);
        }
    }

    private boolean validateRequest(HttpServletRequest request) {
        RequestParametersValidator validator = new RequestParametersValidator(request);

        return validator.validatePresence("name", "email", "password", "countryCode", "phoneNumber")
                && validator.validateEmail("email");
    }

    private void preserveStatusRequest(
            HttpServletRequest request,
            String name,
            String email,
            String countryCode,
            String phoneNumber) {
        request.setAttribute("name", name);
        request.setAttribute("email", email);
        request.setAttribute("countryCode", countryCode);
        request.setAttribute("phoneNumber", phoneNumber);
    }
}
