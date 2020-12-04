package com.ibm.mydev.personaldata.infrasctructure.mydev.api;

import com.ibm.mydev.personaldata.domain.developmentactions.DevelopmentAction;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTrainingLocalView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTrainingView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTranscriptView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevUserView;

import java.util.List;

public interface IMyDevApiClient {

    MyDevUserView getUserData(String uid);

    MyDevTranscriptView getTranscriptData(Integer id, Integer year);

    MyDevTrainingView getTrainingData(List<String> objectId);

    MyDevTrainingLocalView getTrainingLocalData(Integer cultureId, List<String> objectIds);

    List<DevelopmentAction> buildDevelopmentActions(MyDevUserView userView, MyDevTranscriptView transcriptView, MyDevTrainingView trainingView, MyDevTrainingLocalView trainingLocalView);

}
