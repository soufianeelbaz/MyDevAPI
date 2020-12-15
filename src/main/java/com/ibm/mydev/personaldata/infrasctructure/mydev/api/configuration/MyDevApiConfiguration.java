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

@Profile("MYDEV_REAL")
@Configuration
public class MyDevApiConfiguration {

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

    @Bean
    public MyDevTokenService myDevTokenService() {
        return new MyDevTokenService(myDevOAuthRestTemplate());
    }

    @Bean
    @Qualifier("MyDevRestTemplate")
    public RestTemplate myDevRestTemplate(RestTemplateBuilder builder) {
        builder.setConnectTimeout(timeout);
        builder.setReadTimeout(timeout);
        builder.customizers(customRestTemplateCustomizer());
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
