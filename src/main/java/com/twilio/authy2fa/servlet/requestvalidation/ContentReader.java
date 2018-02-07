package com.twilio.authy2fa.servlet.requestvalidation;

import org.apache.http.HttpResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Class for reading a HttpResponse.
 *
 * @see HttpResponse
 *
 * @author Agustin Camino
 */
public class ContentReader {

    /**
     * Attempts to read the {@link HttpServletRequest} into a {@link StringBuffer}.
     *
     * @param request   The HTTP Request
     *
     * @return The string representation of the characters added to the buffer
     *
     * @throws IOException
     */
    public String read(HttpServletRequest request) throws IOException {
        BufferedReader bufferedReader = request.getReader();

        return readContentFrom(bufferedReader);
    }

    /**
     * Attempts to read the {@link HttpResponse} into a {@link StringBuffer}.
     *
     * @param response   The HTTP Response
     *
     * @return The string representation of the characters added to the buffer
     *
     * @throws IOException
     */
    public String read(HttpResponse response) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        return readContentFrom(bufferedReader);
    }

    private String readContentFrom(BufferedReader bufferedReader) throws IOException {

        StringBuffer result = new StringBuffer();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }

}