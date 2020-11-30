package com.ibm.mydev.api.configuration;

import com.ibm.mydev.api.interceptor.MyDevClientHttpRequestInterceptor;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.web.client.RestTemplate;

public class MyDevRestTemplateCustomizer implements RestTemplateCustomizer {

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add(new MyDevClientHttpRequestInterceptor());
    }
}