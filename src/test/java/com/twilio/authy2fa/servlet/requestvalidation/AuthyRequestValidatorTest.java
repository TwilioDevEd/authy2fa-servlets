package com.twilio.authy2fa.servlet.requestvalidation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class AuthyRequestValidatorTest {
    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "approved", true, "approved", "wG/FBuwrfLVEJuCD9MduetPbxPw1Ela82ARYFt5p9nQ=" },
                { "denied", true, "denied", "ZbF63opTd50rMwSoXm/bBci+wR7xWZJS0ghDr1rvvMc=" },
                { "unauthorized", false, "approved", "Made-Up Authy Signature" }
        });
    }

    private String expected;
    private boolean expectedValidity;
    private String oneTouchStatus;
    private String authySignature;

    public AuthyRequestValidatorTest(String expected, boolean expectedValidity, String oneTouchStatus, String authySignature) {
        this.expected = expected;
        this.expectedValidity = expectedValidity;
        this.oneTouchStatus = oneTouchStatus;
        this.authySignature = authySignature;
    }

    @Test
    public void validatesOneTouchRequest() throws IOException {
        String content =
                "{\n" +
                        "    \"authy_id\": 12345,\n" +
                        "    \"device_uuid\": 73125466,\n" +
                        "    \"callback_action\": \"approval_request_status\",\n" +
                        "    \"uuid\": \"xxxxx-f798-xxxx-b3cf-12ce654b4414\",\n" +
                        "    \"status\": \"" + oneTouchStatus + "\",\n" +
                        "    \"approval_request\": {\n" +
                        "        \"transaction\": {\n" +
                        "            \"details\": {\n" +
                        "                \"email\": \"aaa@aaa.com\"\n" +
                        "            },\n" +
                        "            \"device_details\": {},\n" +
                        "            \"device_geolocation\": \"\",\n" +
                        "            \"device_signing_time\": 1519040875,\n" +
                        "            \"encrypted\": false,\n" +
                        "            \"flagged\": false,\n" +
                        "            \"hidden_details\": null,\n" +
                        "            \"message\": \"Request login to Twilio demo app\",\n" +
                        "            \"reason\": \"\",\n" +
                        "            \"requester_details\": null,\n" +
                        "            \"status\": \"approved\",\n" +
                        "            \"uuid\": \"608d4190-xxx-0135-b3cf-12ce654b4414\",\n" +
                        "            \"created_at_time\": 1519040759,\n" +
                        "            \"customer_uuid\": 43727\n" +
                        "        },\n" +
                        "        \"expiration_timestamp\": 1519127159,\n" +
                        "        \"logos\": null,\n" +
                        "        \"app_id\": \"5833393871fc61111c76d84f\"\n" +
                        "    },\n" +
                        "    \"signature\": \"iT1KBQdaJxfxxxxxxxpNRTrmUj4C6t19WxJeo+Iq7MMqZijZ5+7FdfISP3tRXE3YZKHK/AC81hUOgUMTZ9xvX2H6L9YHlqjS/lZSmo1nrBIIGCZJiGAJzpE3sNzHZqUi7qzZobMCGCqAvGhPGIm7P6d4MvnTe9UBaYnUpQLtnwLW9OA5jIaBDbkX7yfgPpDME+9LKukEVIaojZUEpxeFqcov2J5Utkqe4YVTxs1oJEyi4FltXBca9CuQ3Jn1B+5TPf62xt/kCTPs3yBIKoHc20rR/Q7SYomxxOQ6fk3vz3eIL5tt/nFcKht1pibFv3W3L4+g==\",\n" +
                        "    \"device\": {\n" +
                        "        \"city\": \"Taipei\",\n" +
                        "        \"country\": \"Taiwan\",\n" +
                        "        \"ip\": \"9.27.3.18\",\n" +
                        "        \"region\": \"Taipei City\",\n" +
                        "        \"registration_city\": null,\n" +
                        "        \"registration_country\": \"Taiwan\",\n" +
                        "        \"registration_ip\": \"9.27.3.18\",\n" +
                        "        \"registration_method\": \"sms\",\n" +
                        "        \"registration_region\": null,\n" +
                        "        \"os_type\": \"android\",\n" +
                        "        \"last_account_recovery_at\": null,\n" +
                        "        \"id\": 123456,\n" +
                        "        \"registration_date\": 1519040556,\n" +
                        "        \"last_sync_date\": 1519040763\n" +
                        "    }\n" +
                        "}";

        InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

        when(request.getReader()).thenReturn(bufferedReader);
        when(request.getHeader("X-Authy-Signature-Nonce")).thenReturn("1519040876");
        when(request.getHeader("X-Authy-Signature")).thenReturn(authySignature);
        when(request.getMethod()).thenReturn("POST");
        when(request.getScheme()).thenReturn("https");
        when(request.getServerName()).thenReturn("db8ae627.ngrok.io");
        when(request.getServletPath()).thenReturn("/authy/callback");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://db8ae627.ngrok.io/authy/callback"));

        AuthyRequestValidator validator = new AuthyRequestValidator("auth-token", request);

        RequestValidationResult result = validator.validate();

        assertEquals(expected, result.getStatus());
        assertEquals(12345, result.getAuthyId());
        assertEquals((Boolean) expectedValidity, result.isValidSignature());
    }
}
