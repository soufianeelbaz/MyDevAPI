package com.ibm.mydev.personaldata.domain.developmentactions;

import com.google.common.collect.Lists;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.configuration.MyDevApiConfiguration;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.MyDevApiClient;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MyDevDevelopmentActions implements DevelopmentActions {

    private static Logger LOGGER = LoggerFactory
            .getLogger(MyDevDevelopmentActions.class);

    @Autowired
    public MyDevApiClient myDevApiClient;

    @Autowired
    public MyDevApiConfiguration myDevApiConfiguration;

    private CompletableFuture<MyDevTranscriptView> getUserTranscriptsAsync(MyDevUserView user, Integer year) {
        CompletableFuture<MyDevTranscriptView> userTranscripts = CompletableFuture.supplyAsync(() -> myDevApiClient.getTranscriptData(user.getValue().get(0).getId(), year));
        return userTranscripts;
    }

    private CompletableFuture<List<List<String>>> getObjectIdChunksAsync(MyDevTranscriptView transcripts) {
        return CompletableFuture.supplyAsync(() -> {
            if (!MyDevView.isEmpty(transcripts)) {
                List<String> objectIds = transcripts.getValue()
                        .stream()
                        .map(transcriptItem -> transcriptItem.getTrainingId())
                        .filter(trainingId -> trainingId != null && !trainingId.isEmpty())
                        .collect(Collectors.toList());
                return Lists.partition(objectIds, myDevApiConfiguration.chunkUrlSize);
            } else {
                return Collections.emptyList();
            }
        });
    }

    private static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allDoneFuture =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allDoneFuture.thenApply(v ->
                futures.stream().
                        map(future -> future.join()).
                        collect(Collectors.<T>toList())
        );
    }



    private List<CompletableFuture<MyDevTrainingsAndTrainingLocals>> getChunkedTrainingAndTrainingLocalFutures(Integer cultureId,
                                                                                                               List<List<String>> objectIdsChunks) {

        List<CompletableFuture<MyDevTrainingsAndTrainingLocals>> futures = new ArrayList<>();

        List<CompletableFuture<MyDevTrainingsAndTrainingLocals>> trainings =  objectIdsChunks
                .stream()
                .filter(objectIdsChunk -> objectIdsChunk != null && !objectIdsChunk.isEmpty())
                .map(objectIdsChunk -> CompletableFuture.supplyAsync(() -> {
                    MyDevTrainingView training = myDevApiClient.getTrainingData(objectIdsChunk);
                    MyDevTrainingsAndTrainingLocals trainingOrLocal = new MyDevTrainingsAndTrainingLocals();
                    trainingOrLocal.setTraining(training);
                    return trainingOrLocal;
                }))
                .collect(Collectors.toList());
        futures.addAll(trainings);

        if (cultureId != null) {
            List<CompletableFuture<MyDevTrainingsAndTrainingLocals>> trainingLocals =  objectIdsChunks
                    .stream()
                    .filter(objectIdsChunk -> objectIdsChunk != null && !objectIdsChunk.isEmpty())
                    .map(objectIdsChunk -> CompletableFuture.supplyAsync(() -> {
                        MyDevTrainingLocalView trainingLocal = myDevApiClient.getTrainingLocalData(cultureId, objectIdsChunk);
                        MyDevTrainingsAndTrainingLocals trainingOrLocal = new MyDevTrainingsAndTrainingLocals();
                        trainingOrLocal.setTrainingLocal(trainingLocal);
                        return trainingOrLocal;
                    }))
                    .collect(Collectors.toList());

            futures.addAll(trainingLocals);
        }
        return futures;
    }

    @Override
    public List<TrainingItem> getDevelopmentActions(String uid, Integer year) {
        long startTimeMillis = System.currentTimeMillis();
        MyDevUserView userView = myDevApiClient.getUserData(uid);
        if (!MyDevView.isEmpty(userView)) {
            UserItem user = userView.getValue().get(0);
            CompletableFuture<List<MyDevTrainingsAndTrainingLocals>> trainingsAndLocalsFuture =
                    getUserTranscriptsAsync(userView, year)
                            .thenCompose((transcripts) -> getObjectIdChunksAsync(transcripts))
                            .thenCompose((objectIdChunks) -> sequence(getChunkedTrainingAndTrainingLocalFutures(user.getLanguageId(), objectIdChunks)));
            try {

                List<MyDevTrainingsAndTrainingLocals> trainingsAndLocals = trainingsAndLocalsFuture.get();

                List<TrainingItem> trainings = trainingsAndLocals
                        .stream()
                        .filter(trainingOrLocal -> trainingOrLocal.getTraining() != null && trainingOrLocal.getTraining().getValue() != null)
                        .map(trainingOrLocal -> trainingOrLocal.getTraining().getValue())
                        .flatMap(Collection::stream)
                        .filter(trainingItem -> trainingItem.getObjectId() != null)
                        .collect(Collectors.toList());

                Map<String, TrainingLocalItem> trainingLocalsMappedById = trainingsAndLocals
                        .stream()
                        .filter(trainingOrLocal -> trainingOrLocal.getTrainingLocal() != null && trainingOrLocal.getTrainingLocal().getValue() != null)
                        .map(trainingOrLocal -> trainingOrLocal.getTrainingLocal().getValue())
                        .flatMap(Collection::stream)
                        .filter(trainingLocalItem -> trainingLocalItem.getObjectId() != null)
                        .collect(Collectors.toMap(TrainingLocalItem::getObjectId, Function.identity()));

                long endTimeMillis = System.currentTimeMillis();
                LOGGER.info("The request took : " + ((endTimeMillis - startTimeMillis) / 1000) + " seconds.");

                return trainings; // todo: transformation la liste des formation mydev en actions de développement.

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return Collections.emptyList();
        }
    }
}
