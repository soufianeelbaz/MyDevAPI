package com.ibm.mydev.personaldata.infrasctructure.mydev.api.configuration;

import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.interceptor.MyDevClientHttpOAuthInterceptor;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.token.MyDevTokenRequestBody;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.token.MyDevTokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Profile("!MYDEV_MOCK")
@Configuration
public class MyDevApiConfiguration {

    @Value("${mydev.csod.api}")
    public String baseUrl;

    @Value("${mydev.csod.api.credentials.clientId}")
    public String clientId;

    @Value("${mydev.csod.api.credentials.clientSecret}")
    public String clientSecret;

    @Value("${mydev.csod.api.endpoints.auth}")
    public String authentication;

    @Value("${mydev.csod.api.endpoints.trainings}")
    public String trainings;

    @Value("${mydev.csod.api.endpoints.trainingsLocal}")
    public String trainingsLocal;

    @Value("${mydev.csod.api.endpoints.transcripts}")
    public String transcripts;

    @Value("${mydev.csod.api.endpoints.users}")
    public String users;

    @Value("${mydev.csod.api.timeout:30}")
    public int timeout;

    @Value("${mydev.csod.api.chunk.url.size}")
    public int chunkUrlSize;

    @Bean
    public RestTemplateCustomizer customRestTemplateCustomizer() {
        return new MyDevRestTemplateCustomizer();
    }

    @Bean
    @Qualifier("MyDevOAuthRestTemplate")
    public RestTemplate myDevOAuthRestTemplate() {
        return new RestTemplate();
    }

    private MyDevTokenRequestBody getAuthRequestBody() {
        MyDevTokenRequestBody body = new MyDevTokenRequestBody();
        body.setClientId(clientId);
        body.setClientSecret(clientSecret);
        body.setGrantType("client_credentials");
        body.setScope("all");
        return body;
    }

    @Bean
    public MyDevTokenService myDevTokenService() {
        return new MyDevTokenService(myDevOAuthRestTemplate(), baseUrl + authentication , getAuthRequestBody());
    }

    @Bean
    @Qualifier("MyDevRestTemplate")
    public RestTemplate myDevRestTemplate(RestTemplateBuilder builder) {
        builder.setConnectTimeout(timeout);
        builder.setReadTimeout(timeout);
        builder.customizers(customRestTemplateCustomizer());
        builder.rootUri(baseUrl);
        RestTemplate restTemplate = builder.build();
        List<ClientHttpRequestInterceptor> interceptors
                = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new MyDevClientHttpOAuthInterceptor(myDevTokenService()));
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

}
