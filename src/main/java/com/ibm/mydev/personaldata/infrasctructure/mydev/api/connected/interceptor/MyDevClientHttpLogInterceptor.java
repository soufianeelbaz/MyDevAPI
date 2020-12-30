package com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.interceptor;

import com.ibm.mydev.personaldata.infrasctructure.mydev.api.configuration.MyDevClientException;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.utils.MyDevResponseCodeUtil;
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
            ClientHttpRequestExecution execution) throws IOException, MyDevClientException {

        ClientHttpResponse response = execution.execute(request, body);
        logRequestAndResponseDetails(request, response);
        throwExceptionIfRequestFailed(response);
        return response;
    }

    private void logRequestAndResponseDetails(HttpRequest request, ClientHttpResponse response) throws IOException {
        LOGGER.info("Headers: {}", request.getHeaders());
        LOGGER.info("Request Method: {}", request.getMethod());
        LOGGER.info("Request URI: {}", request.getURI());
        LOGGER.info("Response status code: {}", response.getStatusCode());
        LOGGER.info("Response status text: {} {}", MyDevResponseCodeUtil.getCSODHttpStatusMessage(response.getRawStatusCode(), response.getStatusText()), System.getProperty("line.separator"));
    }

    private void throwExceptionIfRequestFailed(ClientHttpResponse response) throws IOException, MyDevClientException {
        int code = response.getRawStatusCode();
        if (code != 200) {
            String msg = MyDevResponseCodeUtil.getCSODHttpStatusMessage(response.getRawStatusCode(), response.getStatusText());
            throw new MyDevClientException(code, msg);
        }
    }
}