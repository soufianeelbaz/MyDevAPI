package com.ibm.mydev.personaldata.infrasctructure.mydev.api.configuration;

import org.springframework.web.client.RestClientException;

public class MyDevClientException extends RestClientException {

    private final int code;

    public MyDevClientException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
