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

    private static final String ERROR_400 = "CSOD Bad Request.";
    private static final String ERROR_401 = "CSOD Unauthorized.";
    private static final String ERROR_404 = "CSOD Not Found.";
    private static final String ERROR_429 = "CSOD Too many requests.";
    private static final String ERROR_50_ = "CSOD Internal Server Error.";

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
        LOGGER.info("Response status text: {} {}", getCSODHttpStatusMessage(response), System.getProperty("line.separator"));
    }

    private String getCSODHttpStatusMessage(ClientHttpResponse response) throws IOException {
        int status = response.getRawStatusCode();
        String message = null;
        if (status != 200) {
            switch (status) {
                case 400:
                    message = ERROR_400;
                    break;
                case 401:
                    message = ERROR_401;
                    break;
                case 404:
                    message = ERROR_404;
                    break;
                case 429:
                    message = ERROR_429;
                    break;
                case 500:
                case 503:
                case 504:
                    message = ERROR_50_;
                    break;
                default:
                    break;
            }
        }
        if (message == null) {
            message = response.getStatusText();
        }
        return message;
    }
}