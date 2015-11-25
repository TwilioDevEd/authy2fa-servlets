package com.twilio.authy2fa.servlet.authy;

import com.twilio.authy2fa.lib.SessionManager;
import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;
import lib.MockServletOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

public class OneTouchStatusServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void writesTheAuthyStatusOnTheServletOutputStream() throws ServletException, IOException {
        long userId = 1;

        SessionManager sessionManager = mock(SessionManager.class);
        when(sessionManager.getLoggedUserId(request)).thenReturn(userId);

        User user = new User();
        user.setAuthyStatus("approved");
        UserService userService = mock(UserService.class);
        when(userService.find(userId)).thenReturn(user);

        OneTouchStatusServlet servlet = new OneTouchStatusServlet(sessionManager, userService);
        MockServletOutputStream mockServletOutputStream = new MockServletOutputStream();

        when(response.getOutputStream()).thenReturn(mockServletOutputStream);
        servlet.doPost(request, response);

        assertEquals("approved", mockServletOutputStream.toString());
    }
}
