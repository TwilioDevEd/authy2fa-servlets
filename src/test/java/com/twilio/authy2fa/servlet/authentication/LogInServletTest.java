package com.twilio.authy2fa.servlet.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.authy2fa.exception.ApprovalRequestException;
import com.twilio.authy2fa.lib.SessionManager;
import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;
import com.twilio.authy2fa.service.ApprovalRequestService;
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
    private ApprovalRequestService approvalRequestService;

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

        subject = new LogInServlet(sessionManager, userService, approvalRequestService,
                new ObjectMapper());
    }


    @Test
    public void requestApprovalRequestWhenCredentialsAreValid()
            throws ServletException, IOException {
        // Given
        when(request.getParameter("password")).thenReturn(password);
        when(userService.findByEmail(anyString())).thenReturn(user);

        when(approvalRequestService.sendApprovalRequest(user))
                .thenReturn("sms");

        // When
        subject.doPost(request, response);

        // Then
        verify(sessionManager).partialLogIn(request, user.getId());
        verify(approvalRequestService).sendApprovalRequest(any());
        assertEquals("{\"result\":\"SMS\",\"message\":null}",
                mockServletOutputStream.toString());
    }

    @Test
    public void handleApprovalRequestException()
            throws ServletException, IOException {
        // Given
        when(request.getParameter("password")).thenReturn(password);
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(approvalRequestService.sendApprovalRequest(user))
                .thenThrow(new ApprovalRequestException("exception message"));

        // When
        subject.doPost(request, response);

        // Then
        verify(sessionManager).partialLogIn(request, user.getId());
        verify(approvalRequestService).sendApprovalRequest(any());
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
        verify(approvalRequestService, never()).sendApprovalRequest(any());
        assertEquals("{\"result\":\"ERROR\",\"message\":\"Invalid Credentials\"}",
                mockServletOutputStream.toString());
    }
}
