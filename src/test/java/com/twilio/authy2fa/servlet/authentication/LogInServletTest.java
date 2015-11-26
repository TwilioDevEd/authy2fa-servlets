package com.twilio.authy2fa.servlet.authentication;

import com.authy.onetouch.Client;
import com.authy.onetouch.approvalrequest.Parameters;
import com.authy.onetouch.approvalrequest.Result;
import com.twilio.authy2fa.lib.SessionManager;
import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;
import lib.MockServletOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LogInServletTest {
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

    @Mock
    private Client oneTouchClient;

    @Mock
    private Result result;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendsApprovalRequestWhenCredentialsAreValidThenRespondsWithOneTouchIfResponseIsSuccessful()
            throws ServletException, IOException {
        String password = "secret";
        User user = new User();
        user.setPassword(password);
        when(request.getParameter("password")).thenReturn(password);
        when(userService.findByEmail(anyString())).thenReturn(user);

        when(result.isOk()).thenReturn(true);
        when(oneTouchClient.sendApprovalRequest(anyString(), anyString(), any(Parameters.class)))
                .thenReturn(result);

        MockServletOutputStream mockServletOutputStream = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(mockServletOutputStream);


        LogInServlet servlet = new LogInServlet(sessionManager, userService, oneTouchClient);
        servlet.doPost(request, response);

        verify(sessionManager).partialLogIn(request, user.getId());
        verify(oneTouchClient).sendApprovalRequest(
                anyString(),
                anyString(),
                any(Parameters.class));
        assertEquals("onetouch", mockServletOutputStream.toString());
    }

    @Test
    public void sendsApprovalRequestWhenCredentialsAreValidThenRespondsWithSMSIfResponseIsUnsuccessful()
            throws ServletException, IOException {
        String password = "secret";
        User user = new User();
        user.setPassword(password);
        when(request.getParameter("password")).thenReturn(password);
        when(userService.findByEmail(anyString())).thenReturn(user);

        when(result.isOk()).thenReturn(false);
        when(oneTouchClient.sendApprovalRequest(anyString(), anyString(), any(Parameters.class)))
                .thenReturn(result);

        MockServletOutputStream mockServletOutputStream = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(mockServletOutputStream);


        LogInServlet servlet = new LogInServlet(sessionManager, userService, oneTouchClient);
        servlet.doPost(request, response);

        verify(sessionManager).partialLogIn(request, user.getId());
        verify(oneTouchClient).sendApprovalRequest(
                anyString(),
                anyString(),
                any(Parameters.class));
        assertEquals("sms", mockServletOutputStream.toString());
    }

    @Test
    public void doNotSendApprovalRequestWhenCredentialsAreInvalid() throws ServletException, IOException {
        String password = "secret";
        User user = new User();
        user.setPassword(password);
        when(request.getParameter("password")).thenReturn("s3cre7");
        when(userService.findByEmail(anyString())).thenReturn(user);

        MockServletOutputStream mockServletOutputStream = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(mockServletOutputStream);

        LogInServlet servlet = new LogInServlet(sessionManager, userService, oneTouchClient);
        servlet.doPost(request, response);

        verify(sessionManager, never()).partialLogIn(request, user.getId());
        verify(oneTouchClient, never()).sendApprovalRequest(
                anyString(),
                anyString(),
                any(Parameters.class));

        assertEquals("unauthorized", mockServletOutputStream.toString());
    }
}
