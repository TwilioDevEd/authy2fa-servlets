package com.authy.lib.onetouch;

import com.authy.onetouch.RequestValidator;
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
public class RequestValidatorTest {
    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "approved", "approved", "lbf3Tc/4EqrugZN3k1QlZc49TvH9aAI8Bb7ex1oE80I=" },
                { "denied", "denied", "MNKi4IgPi6o/7alqZaF39Yk6+2eg/VnfkykpQO7raC0=" },
                { "unauthorized", "approved", "Made-Up Authy Signature" }
        });
    }

    private String expected;
    private String oneTouchStatus;
    private String authySignature;

    public RequestValidatorTest(String expected, String oneTouchStatus, String authySignature) {
        this.expected = expected;
        this.oneTouchStatus = oneTouchStatus;
        this.authySignature = authySignature;
    }

    @Test
    public void validatesOneTouchRequest() throws IOException {
        String content =
                "{  \n" +
                "   \"device_uuid\":\"9ebe1600-xxxx-xxxx-xxxx-0e67b818e6fb\",\n" +
                "   \"callback_action\":\"approval_request_status\",\n" +
                "   \"uuid\":\"539dc860-xxxx-xxxx-xxxx-0e67b818e6fb\",\n" +
                "   \"status\":\"" + oneTouchStatus +"\",\n" +
                "   \"approval_request\":{  \n" +
                "      \"transaction\":{  \n" +
                "         \"details\":{  \n" +
                "            \"email\":\"bob@example.com\"\n" +
                "         },\n" +
                "         \"device_details\":null,\n" +
                "         \"device_geolocation\":null,\n" +
                "         \"device_signing_time\":0,\n" +
                "         \"encrypted\":false,\n" +
                "         \"flagged\":false,\n" +
                "         \"hidden_details\":{  \n" +
                "\n" +
                "         },\n" +
                "         \"message\":\"Request login to Twilio demo app\",\n" +
                "         \"reason\":null,\n" +
                "         \"requester_details\":null,\n" +
                "         \"status\":\"" + oneTouchStatus + "\",\n" +
                "         \"uuid\":\"539dc860-xxxx-xxxx-xxxx-0e67b818e6fb\",\n" +
                "         \"created_at_time\":1448028177,\n" +
                "         \"customer_uuid\":\"5ccf0040-xxxx-xxxx-xxxx-0e67b818e6fb\"\n" +
                "      },\n" +
                "      \"logos\":null,\n" +
                "      \"expiration_timestamp\":1448114577\n" +
                "   },\n" +
                "   \"signature\":\"mAk+GMTF4UwvbBZILtQMa/mPOw6O5w489zybTvxLCcU3ua6oIoHuxj9bOmpACOXUyasQ1woOVo3IJjSZcsiLC7aXZn94DArDJxx6fnITX4FbHXgPPCpQoIbdCcBCS2zxfIodW/yTZqH1OdOKz5Hny4i8xmbriqcusPx3B1e7rNIpHIMHnPWkDiU8qOkkxNRijPjN612oAyt9kfqv4ISz26Fw44t0xZaXsN/0lGZfU9zM70JuQ1ixcB1WVJMdST7Wz4NK61bmA3aB40sWDSk2hBLSKbK/J6bbEGaltyuNykM9o0t/JNQFPgVY4KdRuORy6oXbmUxjDAtJdV8T7CAUZw==\",\n" +
                "   \"authy_id\":8190000\n" +
                "}";

        InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

        when(request.getReader()).thenReturn(bufferedReader);
        when(request.getHeader("X-Authy-Signature-Nonce")).thenReturn("Authy-Signature-Nonce");
        when(request.getHeader("X-Authy-Signature")).thenReturn(authySignature);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://example.com"));

        RequestValidator validator = new RequestValidator("Authy-Api-Key", request);

        String result = validator.validate();

        assertEquals(expected, result);
    }
}
