package com.ibm.mydev.api.token;

import com.ibm.mydev.api.configuration.MyDevApiConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MyDevTokenService implements IMyDevTokenService {

    private static final String CLIENT_CREDENTIALS = "client_credentials";
    private static final String ALL = "all";

    private RestTemplate restTemplate;

    @Autowired
    public MyDevApiConfiguration apiConfiguration;

    public MyDevTokenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getAccessToken() {
        HttpHeaders headers = getHeaders();
        HttpEntity request = new HttpEntity(getRequestBody(), headers);
        ResponseEntity<MyDevTokenResponseBody> response = restTemplate.exchange(
                apiConfiguration.baseUrl + apiConfiguration.authentication, HttpMethod.POST,
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

    private MyDevTokenRequestBody getRequestBody() {
        MyDevTokenRequestBody body = new MyDevTokenRequestBody();
        body.setClientId(apiConfiguration.clientId);
        body.setClientSecret(apiConfiguration.clientSecret);
        body.setGrantType(CLIENT_CREDENTIALS);
        body.setScope(ALL);
        return body;
    }
}
