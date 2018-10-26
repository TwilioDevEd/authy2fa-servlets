package com.twilio.authy2fa.servlet.authy;

import com.authy.AuthyApiClient;
import com.authy.api.Token;
import com.authy.api.Tokens;
import com.twilio.authy2fa.lib.SessionManager;
import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class VerifyCodeServletTest {

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
    private Tokens tokens;

    @Mock
    private Token token;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenTheProvidedCodeIsValidThenLogsInAndRedirectToAccount() throws Exception {

        User alice = new User();
        alice.setId(1101);
        alice.setAuthyId(800001);
        when(userService.find(anyLong())).thenReturn(alice);
        when(authyClient.getTokens()).thenReturn(tokens);

        when(token.isOk()).thenReturn(true);
        when(tokens.verify(anyInt(), anyString())).thenReturn(token);

        VerifyCodeServlet servlet = new VerifyCodeServlet(sessionManager, userService, authyClient);
        servlet.doPost(request, response);

        verify(sessionManager, times(1)).logIn(request, alice.getId());
        verify(response).sendRedirect("/account");
    }

    @Test
    public void whenTheProvidedCodeIsInvalidThenLogsInAndRedirectToAccount() throws Exception {

        User alice = new User();
        alice.setId(1101);
        alice.setAuthyId(800001);
        when(userService.find(anyLong())).thenReturn(alice);
        when(authyClient.getTokens()).thenReturn(tokens);

        when(token.isOk()).thenReturn(false);
        when(tokens.verify(anyInt(), anyString())).thenReturn(token);

        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        VerifyCodeServlet servlet = new VerifyCodeServlet(sessionManager, userService, authyClient);
        servlet.doPost(request, response);

        verify(sessionManager, times(1)).logOut(request);
        verify(sessionManager, never()).logIn(any(HttpServletRequest.class), anyInt());
        verify(request).getRequestDispatcher("/login.jsp");
    }
}