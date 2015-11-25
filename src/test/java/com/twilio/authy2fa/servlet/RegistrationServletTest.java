package com.twilio.authy2fa.servlet;

import com.authy.AuthyApiClient;
import com.authy.api.Users;
import com.twilio.authy2fa.lib.SessionManager;
import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegistrationServletTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private UserService userService;

    @Mock
    private AuthyApiClient authyClient;

    @Mock
    private Users users;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void registerAUserLocallyAndRemotely() throws ServletException, IOException {
        when(request.getParameter("name")).thenReturn("Bob");
        when(request.getParameter("email")).thenReturn("bob@example.com");
        when(request.getParameter("password")).thenReturn("s3cre7");
        when(request.getParameter("countryCode")).thenReturn("1");
        when(request.getParameter("phoneNumber")).thenReturn("555-5555");

        User bob = new User();
        when(userService.create(any(User.class))).thenReturn(bob);

        when(authyClient.getUsers()).thenReturn(users);
        com.authy.api.User authyUser = new com.authy.api.User();
        authyUser.setStatus(200);
        authyUser.setId(1101);
        when(users.createUser(anyString(), anyString(), anyString())).thenReturn(authyUser);

        RegistrationServlet servlet = new RegistrationServlet(sessionManager, userService, authyClient);
        servlet.doPost(request, response);

        verify(userService).create(any(User.class));
        verify(users).createUser(anyString(), anyString(), anyString());
        verify(userService).update(any(User.class));
        verify(sessionManager).logIn(request, bob.getId());
        verify(response).sendRedirect("/account");
    }

    @Test
    public void doNotRegisterAUserWhenInformationIsIncomplete() throws ServletException, IOException {
        when(request.getParameter("name")).thenReturn("Bob");
        when(request.getParameter("email")).thenReturn("bob@example.com");
        when(request.getParameter("password")).thenReturn("");
        when(request.getParameter("countryCode")).thenReturn("1");
        when(request.getParameter("phoneNumber")).thenReturn("");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        RegistrationServlet servlet = new RegistrationServlet(sessionManager, userService, authyClient);
        servlet.doPost(request, response);

        verify(userService, never()).create(any(User.class));
        verify(request).getRequestDispatcher("/registration.jsp");
    }
}
