package com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.interceptor;

import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.token.IMyDevTokenService;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.token.MyDevTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class MyDevClientHttpOAuthInterceptor implements ClientHttpRequestInterceptor {

    private IMyDevTokenService tokenService;

    public MyDevClientHttpOAuthInterceptor(IMyDevTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        if (HttpStatus.UNAUTHORIZED == response.getStatusCode()) {
            tokenService.refreshToken();
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
