package com.ibm.mydev.personaldata.infrasctructure.mydev.api.configuration;

import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.interceptor.MyDevClientHttpLogInterceptor;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.web.client.RestTemplate;

public class MyDevRestTemplateCustomizer implements RestTemplateCustomizer {

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add(new MyDevClientHttpLogInterceptor());
    }
}