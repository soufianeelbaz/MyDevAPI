package com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.token;

import com.ibm.mydev.personaldata.infrasctructure.mydev.api.configuration.MyDevClientException;

import java.io.IOException;

public interface IMyDevTokenService {

    void refreshToken() throws IOException, MyDevClientException;

    String getAccessToken() throws IOException, MyDevClientException;

}
