package com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.token;

import com.ibm.mydev.personaldata.domain.developmentactions.MyDevDevelopmentActions;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.configuration.MyDevApiConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class MyDevTokenService implements IMyDevTokenService {

    private static Logger LOGGER = LoggerFactory
            .getLogger(MyDevDevelopmentActions.class);

    private static String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    private static String SCOPE_ALL = "all";

    @Value("${mydev.csod.api}")
    private String baseUrl;
    @Value("${mydev.csod.api.credentials.clientId}")
    private String clientId;
    @Value("${mydev.csod.api.credentials.clientSecret}")
    private String clientSecret;
    @Value("${mydev.csod.api.endpoints.auth}")
    private String authentication;

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
            LOGGER.error("Token récupéré avec succès: \"{}\"", token);
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