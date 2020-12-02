package com.ibm.mydev.api.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.mydev.api.IMyDevApiClient;
import com.ibm.mydev.dto.MyDevTrainingLocalView;
import com.ibm.mydev.dto.MyDevTrainingView;
import com.ibm.mydev.dto.MyDevTranscriptView;
import com.ibm.mydev.dto.MyDevUserView;
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
    public MyDevTranscriptView getTranscriptData(Long id, Integer year) {
        return unmarshall(transcripts, MyDevTranscriptView.class);
    }

    @Override
    public MyDevTrainingView getTrainingData(List<String> objectId) {
        return unmarshall(trainings, MyDevTrainingView.class);
    }

    @Override
    public MyDevTrainingLocalView getTrainingLocalData(Long cultureId, List<String> objectIds) {
        return unmarshall(trainingLocals, MyDevTrainingLocalView.class);
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
