package com.authy.lib;

import org.apache.http.HttpResponse;

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

        StringBuffer result = new StringBuffer();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }
}
