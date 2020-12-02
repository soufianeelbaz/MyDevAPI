package com.ibm.mydev.api.mock;

import com.ibm.mydev.api.IMyDevApiClient;
import com.ibm.mydev.dto.MyDevTrainingLocalView;
import com.ibm.mydev.dto.MyDevTrainingView;
import com.ibm.mydev.dto.MyDevTranscriptView;
import com.ibm.mydev.dto.MyDevUserView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile("dev")
@Service
public class MyDevMockApiClient implements IMyDevApiClient {

    @Value("classpath:data/users.json")
    public Resource users;

    @Value("classpath:data/transcripts.json")
    public Resource transcripts;

    @Value("classpath:data/trainings.json")
    public Resource trainings;

    @Value("classpath:data/training_locals.json")
    public Resource trainingLocals;

    @Override
    public MyDevUserView getUserData(String uid) {
        return null;
    }

    @Override
    public MyDevTranscriptView getTranscriptData(Long id, Integer year) {
        return null;
    }

    @Override
    public MyDevTrainingView getTrainingData(String objectId) {
        return null;
    }

    @Override
    public MyDevTrainingLocalView getTrainingLocalData(Long cultureId, List<String> objectIds) {
        return null;
    }

}
