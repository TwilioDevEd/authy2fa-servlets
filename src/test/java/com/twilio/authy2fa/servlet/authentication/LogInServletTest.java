package com.twilio.authy2fa.servlet.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.authy2fa.exception.AuthyRequestException;
import com.twilio.authy2fa.lib.SessionManager;
import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;
import com.twilio.authy2fa.service.AuthyRequestService;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class LogInServletTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private UserService userService;

    @Mock
    private AuthyRequestService authyRequestService;

    private MockServletOutputStream mockServletOutputStream;

    private String password;

    private User user;

    private LogInServlet subject;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        password = "secret";
        user = new User();
        user.setPassword(password);

        mockServletOutputStream = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(mockServletOutputStream);

        subject = new LogInServlet(sessionManager, userService, authyRequestService,
                new ObjectMapper());
    }


    @Test
    public void requestApprovalRequestWhenCredentialsAreValid()
            throws ServletException, IOException {
        // Given
        when(request.getParameter("password")).thenReturn(password);
        when(userService.findByEmail(anyString())).thenReturn(user);

        when(authyRequestService.sendApprovalRequest(user))
                .thenReturn("sms");

        // When
        subject.doPost(request, response);

        // Then
        verify(sessionManager).partialLogIn(request, user.getId());
        verify(authyRequestService).sendApprovalRequest(any());
        assertEquals("{\"result\":\"SMS\",\"message\":null}",
                mockServletOutputStream.toString());
    }

    @Test
    public void handleApprovalRequestException()
            throws ServletException, IOException {
        // Given
        when(request.getParameter("password")).thenReturn(password);
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(authyRequestService.sendApprovalRequest(user))
                .thenThrow(new AuthyRequestException("exception message"));

        // When
        subject.doPost(request, response);

        // Then
        verify(sessionManager).partialLogIn(request, user.getId());
        verify(authyRequestService).sendApprovalRequest(any());
        assertEquals("{\"result\":\"ERROR\",\"message\":\"exception message\"}",
                mockServletOutputStream.toString());
    }

    @Test
    public void doNotRequestApprovalRequestWhenCredentialsAreInvalid() throws ServletException, IOException {
        // Given
        when(request.getParameter("password")).thenReturn("s3cre7");
        when(userService.findByEmail(anyString())).thenReturn(user);

        // When
        subject.doPost(request, response);

        // Then
        verify(sessionManager, never()).partialLogIn(request, user.getId());
        verify(authyRequestService, never()).sendApprovalRequest(any());
        assertEquals("{\"result\":\"ERROR\",\"message\":\"Invalid Credentials\"}",
                mockServletOutputStream.toString());
    }
}
