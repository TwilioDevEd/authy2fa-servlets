package com.twilio.authy2fa.servlet.authentication;

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
import static org.mockito.Mockito.*;

public class ConfirmLogInServletTest {

    private static final long USER_ID = 1101;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    RequestDispatcher requestDispatcher;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private UserService userService;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenAuthyStatusIsApprovedThenLogsIn() throws ServletException, IOException {
        User user = new User();
        user.setId(USER_ID);
        user.setAuthyStatus("approved");

        when(userService.find(anyLong())).thenReturn(user);

        ConfirmLogInServlet servlet = new ConfirmLogInServlet(sessionManager, userService);
        servlet.doPost(request, response);

        verify(sessionManager).logIn(request, user.getId());
        verify(sessionManager, never()).logOut(request);

        verify(response).sendRedirect("/account");
    }

    @Test
    public void whenAuthyStatusIsDeniedThenLogsOut() throws ServletException, IOException {
        User user = new User();
        user.setId(USER_ID);
        user.setAuthyStatus("denied");

        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(userService.find(anyLong())).thenReturn(user);

        ConfirmLogInServlet servlet = new ConfirmLogInServlet(sessionManager, userService);
        servlet.doPost(request, response);

        verify(sessionManager).logOut(request);
        verify(sessionManager, never()).logIn(request, USER_ID);

        verify(request).getRequestDispatcher("/login.jsp");
    }

    @Test
    public void whenAuthyStatusIsUnauthorizedThenLogsOut() throws ServletException, IOException {
        User user = new User();
        user.setId(USER_ID);
        user.setAuthyStatus("unauthorized");

        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(userService.find(anyLong())).thenReturn(user);

        ConfirmLogInServlet servlet = new ConfirmLogInServlet(sessionManager, userService);
        servlet.doPost(request, response);

        verify(sessionManager).logOut(request);
        verify(sessionManager, never()).logIn(request, USER_ID);

        verify(request).getRequestDispatcher("/login.jsp");
    }
}
