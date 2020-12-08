package com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.token;

public interface IMyDevTokenService {

    void refreshToken();

    String getAccessToken();

}
