package com.ibm.mydev.api;

import com.ibm.mydev.dto.MyDevTrainingLocalView;
import com.ibm.mydev.dto.MyDevTrainingView;
import com.ibm.mydev.dto.MyDevTranscriptView;
import com.ibm.mydev.dto.MyDevUserView;

import java.util.List;

public interface IMyDevApiClient {

    MyDevUserView getUserData(String uid);

    MyDevTranscriptView getTranscriptData(Long id, Integer year);

    MyDevTrainingView getTrainingData(String objectId);

    MyDevTrainingLocalView getTrainingLocalData(Long cultureId, List<String> objectIds);

}
