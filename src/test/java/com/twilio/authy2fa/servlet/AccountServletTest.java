package com.twilio.authy2fa.servlet;

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

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountServletTest {

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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void showLoggedUserInformation() throws ServletException, IOException {
        User bob = new User();
        bob.setName("Bob");
        bob.setEmail("bob@example.com");
        bob.setPhoneNumber("555-5555");

        when(userService.find(anyLong())).thenReturn(bob);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        AccountServlet servlet = new AccountServlet(sessionManager, userService);
        servlet.doGet(request, response);

        verify(request).setAttribute("name", bob.getName());
        verify(request).setAttribute("email", bob.getEmail());
        verify(request).setAttribute("phoneNumber", bob.getPhoneNumber());
        verify(request).getRequestDispatcher("/account.jsp");
    }
}