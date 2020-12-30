package com.ibm.mydev.personaldata.infrasctructure.mydev.api;

import com.ibm.mydev.personaldata.domain.developmentactions.DevelopmentAction;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.configuration.MyDevClientException;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTrainingLocalView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTrainingView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTranscriptView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevUserView;

import java.io.IOException;
import java.util.List;

public interface IMyDevApiClient {

    MyDevUserView getUserData(String uid) throws IOException, MyDevClientException ;

    MyDevTranscriptView getTranscriptData(Integer id, Integer year) throws IOException, MyDevClientException ;

    MyDevTrainingView getTrainingData(List<String> objectId) throws IOException, MyDevClientException ;

    MyDevTrainingLocalView getTrainingLocalData(Integer cultureId, List<String> objectIds) throws IOException, MyDevClientException ;
}
