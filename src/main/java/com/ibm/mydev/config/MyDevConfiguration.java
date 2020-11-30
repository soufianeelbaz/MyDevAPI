package com.ibm.mydev.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    @Qualifier("baseUrl")
    public String getBaseUrl() throws Exception {
        return baseUrl;
    }

    @Bean
    @Qualifier("authenticationUrl")
    public URL getAuthURL() throws Exception {
        return new URL(new URL(getBaseUrl()), authentication);
    }
}
