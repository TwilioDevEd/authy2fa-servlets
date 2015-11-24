package com.authy.lib;

import org.json.JSONObject;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class JSONEncoderTest {

    @Test
    public void encode() {
        String content =
                "{  \n" +
                        "   \"status\":\"approved\",\n" +
                        "   \"approval_request\":{  \n" +
                        "      \"transaction\":{  \n" +
                        "         \"details\":{  \n" +
                        "            \"email\":\"bob@example.com\",\n" +
                        "            \"code\":\"+/A ==\",\n" +
                        "            \"reason\":null\n" +
                        "         }\n" +
                        "      }\n" +
                        "   }\n" +
                        "}";

        String sortedParams = JSONEncoder.encode(new JSONObject(content));

        String expected = "approval_request%5Btransaction%5D%5Bdetails%5D%5Bcode%5D=%2B%2FA+%3D%3D&" +
                "approval_request%5Btransaction%5D%5Bdetails%5D%5Bemail%5D=bob%40example.com&" +
                "approval_request%5Btransaction%5D%5Bdetails%5D%5Breason%5D=&" +
                "status=approved";

        assertEquals(expected, sortedParams);
    }
}

