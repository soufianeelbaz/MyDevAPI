package com.ibm.mydev.personaldata.infrasctructure.mydev.api;

import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTrainingLocalView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTrainingView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTranscriptView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevUserView;

import java.util.List;

public interface IMyDevApiClient {

    MyDevUserView getUserData(String uid);

    MyDevTranscriptView getTranscriptData(Long id, Integer year);

    MyDevTrainingView getTrainingData(List<String> objectId);

    MyDevTrainingLocalView getTrainingLocalData(Long cultureId, List<String> objectIds);

}
