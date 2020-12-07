package com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.token;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class MyDevTokenService implements IMyDevTokenService {

    private RestTemplate restTemplate;
    private String authUrl;
    private MyDevTokenRequestBody requestBody;
    private String token;

    public MyDevTokenService(RestTemplate restTemplate, String authUrl, MyDevTokenRequestBody requestBody) {
        this.restTemplate = restTemplate;
        this.authUrl = authUrl;
        this.requestBody = requestBody;
    }

    @Override
    public String getAccessToken(Boolean existingTokenInvalid) {
        if (existingTokenInvalid) {
            HttpHeaders headers = getHeaders();
            HttpEntity request = new HttpEntity(requestBody, headers);
            ResponseEntity<MyDevTokenResponseBody> response = restTemplate.exchange(
                    authUrl, HttpMethod.POST,
                    request, MyDevTokenResponseBody.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                token = response.getBody().getAccessToken();
            }
        }
        return token;
    }

    @Override
    public String getAccessToken() {
        if (token == null) {
            return getAccessToken(true);
        }
        return token;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, "application/json");
        headers.set(HttpHeaders.CACHE_CONTROL, "no-cache");
        return headers;
    }
}