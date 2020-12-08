package com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class MyDevClientHttpLogInterceptor implements ClientHttpRequestInterceptor {

    private static Logger LOGGER = LoggerFactory
            .getLogger(MyDevClientHttpLogInterceptor.class);

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body,
            ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        logRequestAndResponseDetails(request, response);
        return response;
    }

    private void logRequestAndResponseDetails(HttpRequest request, ClientHttpResponse response) throws IOException {
        LOGGER.info("Headers: {}", request.getHeaders());
        LOGGER.info("Request Method: {}", request.getMethod());
        LOGGER.info("Request URI: {}", request.getURI());
        LOGGER.info("Response status code: {}", response.getStatusCode());
        LOGGER.info("Response status text: {} {}", response.getStatusText(), System.getProperty("line.separator"));
    }
}