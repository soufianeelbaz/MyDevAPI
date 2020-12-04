package com.ibm.mydev.personaldata.infrasctructure.mydev.api.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mydev.personaldata.domain.developmentactions.DevelopmentAction;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.IMyDevApiClient;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTrainingLocalView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTrainingView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTranscriptView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevUserView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Profile("MYDEV_MOCK")
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
        return unmarshall(users, MyDevUserView.class);
    }

    @Override
    public MyDevTranscriptView getTranscriptData(Integer id, Integer year) {
        return unmarshall(transcripts, MyDevTranscriptView.class);
    }

    @Override
    public MyDevTrainingView getTrainingData(List<String> objectId) {
        return unmarshall(trainings, MyDevTrainingView.class);
    }

    @Override
    public MyDevTrainingLocalView getTrainingLocalData(Integer cultureId, List<String> objectIds) {
        return unmarshall(trainingLocals, MyDevTrainingLocalView.class);
    }

    @Override
    public List<DevelopmentAction> buildDevelopmentActions(MyDevUserView userView, MyDevTranscriptView transcriptView, MyDevTrainingView trainingView, MyDevTrainingLocalView trainingLocalView) {
        return null;
    }

    private <T> T unmarshall(Resource resource, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(resource.getFile(), clazz);
        } catch (IOException e) {
            return null;
        }
    }
}
