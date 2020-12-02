package com.ibm.mydev.api.connected.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.ibm.mydev.api.token.MyDevTokenService;

import java.io.IOException;

public class MyDevClientHttpOAuthInterceptor implements ClientHttpRequestInterceptor {

    private MyDevTokenService tokenService;

    public MyDevClientHttpOAuthInterceptor(MyDevTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        if (HttpStatus.UNAUTHORIZED == response.getStatusCode()) {
            String accessToken = tokenService.getAccessToken();
            if (!StringUtils.isEmpty(accessToken)) {
                request.getHeaders().remove(HttpHeaders.AUTHORIZATION);
                request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
                response = execution.execute(request, body);
            }
        }
        return response;
    }
}
