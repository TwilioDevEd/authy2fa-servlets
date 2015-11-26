package com.twilio.authy2fa.servlet.authy;

import com.authy.AuthyApiClient;
import com.authy.api.Hash;
import com.authy.api.Users;
import com.twilio.authy2fa.lib.SessionManager;
import com.twilio.authy2fa.models.User;
import com.twilio.authy2fa.models.UserService;
import lib.MockServletOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class RequestTokenServletTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

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

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "Ignored: SMS is not needed for smartphones.", "Authy SoftToken" },
                { "Ok", "Authy OneCode" }
        });
    }

    private String responseMessage;
    private String expectedVerificationStrategy;

    public RequestTokenServletTest(String responseMessage, String expectedVerificationStrategy) {
        this.responseMessage = responseMessage;
        this.expectedVerificationStrategy = expectedVerificationStrategy;
    }

    @Test
    public void requestTokenThenDefineTheAppropriateVerificationStrategy() throws Exception {

        User alice = new User();
        alice.setAuthyId("800001");
        when(userService.find(anyLong())).thenReturn(alice);
        when(authyClient.getUsers()).thenReturn(users);

        Hash result = new Hash();
        result.setMessage(responseMessage);
        when(users.requestSms(anyInt())).thenReturn(result);

        MockServletOutputStream servletOutputStream = new MockServletOutputStream();
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        RequestTokenServlet servlet = new RequestTokenServlet(sessionManager, userService, authyClient);

        servlet.doPost(request, response);

        verify(users, times(1)).requestSms(anyInt());
        assertEquals(expectedVerificationStrategy, servletOutputStream.toString());
    }
}