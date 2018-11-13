package com.twilio.authy2fa.service;

import com.authy.AuthyApiClient;
import com.authy.AuthyException;
import com.authy.api.*;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.twilio.authy2fa.exception.AuthyRequestException;
import com.twilio.authy2fa.models.User;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApprovalRequestServiceTest {

    @Mock
    private AuthyApiClient client;

    @Mock
    private ConfigurationService configuration;

    @Mock
    private OneTouch oneTouch;

    @Mock
    private Users users;

    @Mock
    private UserStatus userStatus;

    @Mock
    private OneTouchResponse oneTouchResponse;

    @Mock
    private Hash hash;

    @Mock
    private com.authy.api.Error error;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(8085));

    private static final Integer authyid = 123;
    private static final String apiKey = "apiKey";

    private AuthyRequestService subject;

    private User user;

    @Before
    public void setUp() throws AuthyException {
        MockitoAnnotations.initMocks(this);

        subject = new AuthyRequestService(configuration, client);
        user = new User();
        user.setAuthyId(authyid);
        user.setEmail("email");
        when(configuration.authyApiKey()).thenReturn(apiKey);
        when(client.getOneTouch()).thenReturn(oneTouch);
        when(client.getUsers()).thenReturn(users);
        when(oneTouch.sendApprovalRequest(any())).thenReturn(oneTouchResponse);
        when(users.requestSms(authyid)).thenReturn(hash);
    }

    @Test
    public void sendApprovalRequestWithOneTouch() throws AuthyException {
        // Given
        when(client.getUsers()).thenReturn(users);
        when(users.requestStatus(anyInt())).thenReturn(userStatus);
        when(userStatus.isRegistered()).thenReturn(true);
        when(oneTouchResponse.isSuccess()).thenReturn(true);

        // When
        String verificationStrategy = subject.sendApprovalRequest(user);

        // Then
        assertEquals(verificationStrategy, "onetouch");
        verify(oneTouch).sendApprovalRequest(any());
    }

    @Test
    public void sendApprovalRequestWithOneTouchThrowsExceptionForFailedRequest() throws AuthyException {
        // Given
        when(client.getUsers()).thenReturn(users);
        when(users.requestStatus(anyInt())).thenReturn(userStatus);
        when(userStatus.isRegistered()).thenReturn(true);
        when(oneTouchResponse.isSuccess()).thenReturn(false);
        when(oneTouchResponse.getMessage()).thenReturn("message");

        // When
        try {
            subject.sendApprovalRequest(user);
            fail("Exception expected");
        } catch(AuthyRequestException e) {
            // Then
            assertEquals(e.getMessage(), "message");
            verify(oneTouch).sendApprovalRequest(any());
        }
    }

    @Test
    public void sendApprovalRequestWithSMS() throws AuthyException {
        // Given
        when(client.getUsers()).thenReturn(users);
        when(users.requestStatus(anyInt())).thenReturn(userStatus);
        when(userStatus.isRegistered()).thenReturn(false);
        when(hash.isSuccess()).thenReturn(true);

        // When
        String verificationStrategy = subject.sendApprovalRequest(user);

        // Then
        assertEquals(verificationStrategy, "sms");
        verify(users).requestSms(authyid);
    }

    @Test
    public void sendApprovalRequestWithSmsThrowsExceptionForFailedRequest() throws AuthyException {
        // Given
        when(client.getUsers()).thenReturn(users);
        when(users.requestStatus(anyInt())).thenReturn(userStatus);
        when(userStatus.isRegistered()).thenReturn(false);
        when(hash.isSuccess()).thenReturn(false);
        when(hash.getError()).thenReturn(error);
        when(error.getMessage()).thenReturn("message");

        // When
        try {
            subject.sendApprovalRequest(user);
            fail("Exception expected");
        } catch(AuthyRequestException e) {
            // Then
            assertEquals(e.getMessage(), "message");
            verify(users).requestSms(authyid);
        }
    }
} 