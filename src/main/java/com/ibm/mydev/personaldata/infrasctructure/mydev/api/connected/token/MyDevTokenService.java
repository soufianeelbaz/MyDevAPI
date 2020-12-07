package com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.token;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class MyDevTokenService implements IMyDevTokenService {

    private RestTemplate restTemplate;
    private String authUrl;
    private MyDevTokenRequestBody requestBody;
    private String token; // todo

    public MyDevTokenService(RestTemplate restTemplate, String authUrl, MyDevTokenRequestBody requestBody) {
        this.restTemplate = restTemplate;
        this.authUrl = authUrl;
        this.requestBody = requestBody;
    }

    @Override
    public String getAccessToken() {
        HttpHeaders headers = getHeaders();
        HttpEntity request = new HttpEntity(requestBody, headers);
        ResponseEntity<MyDevTokenResponseBody> response = restTemplate.exchange(
                authUrl, HttpMethod.POST,
                request, MyDevTokenResponseBody.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().getAccessToken();
        }
        return null;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, "application/json");
        headers.set(HttpHeaders.CACHE_CONTROL, "no-cache");
        return headers;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}