package com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.token;

import com.ibm.mydev.personaldata.domain.developmentactions.MyDevDevelopmentActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class MyDevTokenService implements IMyDevTokenService {

    private static Logger LOGGER = LoggerFactory
            .getLogger(MyDevDevelopmentActions.class);

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
    public void forceUpdate() {
        HttpHeaders headers = getHeaders();
        HttpEntity request = new HttpEntity(requestBody, headers);
        ResponseEntity<MyDevTokenResponseBody> response = restTemplate.exchange(
                authUrl, HttpMethod.POST,
                request, MyDevTokenResponseBody.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            token = response.getBody().getAccessToken();
            LOGGER.error("Token récupéré avec succès: {}", token);
        } else {
            LOGGER.error("Erreur lors de la récupération du token: {}", response.getStatusCode());
        }
    }

    @Override
    public String getAccessToken() {
        if (token == null) {
            forceUpdate();
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