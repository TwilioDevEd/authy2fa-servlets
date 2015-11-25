package com.twilio.authy2fa.servlet.authentication;

import com.twilio.authy2fa.lib.SessionManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.verify;

public class LogOutServletTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private SessionManager sessionManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void logOut() throws ServletException, IOException {
        LogOutServlet servlet = new LogOutServlet(sessionManager);

        servlet.doPost(request, response);

        verify(sessionManager).logOut(request);
        verify(response).sendRedirect("login.jsp");
    }
}
