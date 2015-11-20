package com.authy.lib;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.List;

public abstract class HttpClient {

    /**
     * Sends a get request over HTTP.
     *
     * @param url   The destination URL
     *
     * @return The HTTP response
     *
     * @throws IOException
     */
    protected HttpResponse sendGet(String url) throws IOException {

        org.apache.http.client.HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(url);

        HttpResponse response = client.execute(get);
        return response;
    }

    /**
     * Sends a post request over HTTP.
     *
     * @param url          The destination URL
     * @param parameters   The parameters
     *
     * @return The HTTP response
     *
     * @throws IOException
     */
    protected HttpResponse sendPost(String url, List<NameValuePair> parameters) throws IOException {

        org.apache.http.client.HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        post.setEntity(new UrlEncodedFormEntity(parameters));

        HttpResponse response = client.execute(post);
        return response;
    }
}
