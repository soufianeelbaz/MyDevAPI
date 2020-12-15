package com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class MyDevTokenService implements IMyDevTokenService {

    private static Logger LOGGER = LoggerFactory
            .getLogger(MyDevTokenService.class);

    private static String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    private static String SCOPE_ALL = "all";

    @Value("${mydev.csod.api}")
    public String baseUrl;

    @Value("${mydev.csod.api.endpoints.auth}")
    public String authentication;

    @Value("${mydev.csod.api.credentials.clientId}")
    public String clientId;

    @Value("${mydev.csod.api.credentials.clientSecret}")
    public String clientSecret;

    private RestTemplate restTemplate;

    private String token;

    public MyDevTokenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void refreshToken() {
        HttpHeaders headers = getHeaders();
        HttpEntity request = new HttpEntity(getAuthRequestBody(), headers);
        ResponseEntity<MyDevTokenResponseBody> response = restTemplate.exchange(
                baseUrl + authentication, HttpMethod.POST,
                request, MyDevTokenResponseBody.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            token = response.getBody().getAccessToken();
            LOGGER.info("Token récupéré avec succès: \"{}\"", token);
        } else {
            LOGGER.error("Erreur lors de la récupération du token: {}", response.getStatusCode());
        }
    }

    @Override
    public String getAccessToken() {
        if (token == null) {
            refreshToken();
        }
        return token;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, "application/json");
        headers.set(HttpHeaders.CACHE_CONTROL, "no-cache");
        return headers;
    }

    private MyDevTokenRequestBody getAuthRequestBody() {
        MyDevTokenRequestBody body = new MyDevTokenRequestBody();
        body.setClientId(clientId);
        body.setClientSecret(clientSecret);
        body.setGrantType(GRANT_TYPE_CLIENT_CREDENTIALS);
        body.setScope(SCOPE_ALL);
        return body;
    }
}