package com.ibm.mydev.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;

import java.net.HttpURLConnection;
import java.net.URL;

@Configuration
public class MyDevConfiguration {

    @Value("${mydev.csod.api}")
    public String baseUrl;

    @Value("${mydev.csod.api.credentials.clientId}")
    public String clientId;

    @Value("${mydev.csod.api.credentials.password}")
    public String password;

    @Value("${mydev.csod.api.endpoints.auth}")
    public String authentication;

    @Value("${mydev.csod.api.endpoints.trainings}")
    public String trainings;

    @Value("${mydev.csod.api.endpoints.transcripts}")
    public String transcripts;

    @Value("${mydev.csod.api.endpoints.users}")
    public String users;

    @Bean
    public URL getBaseUrl() throws Exception {
        return new URL(baseUrl);
    }

    @Bean
    public URL getAuthUrl() throws Exception {
        return new URL(getBaseUrl(), authentication);
    }

    @Bean
    public HttpURLConnection getAccessTokenURLConnection() throws Exception {
        HttpURLConnection httpConnection = (HttpURLConnection) getAuthUrl().openConnection();
        httpConnection.setUseCaches(false);
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty(HttpHeaders.ACCEPT, "application/json");
        httpConnection.setDoOutput(true);
        httpConnection.setDoInput(true);
        return httpConnection;
    }
}
