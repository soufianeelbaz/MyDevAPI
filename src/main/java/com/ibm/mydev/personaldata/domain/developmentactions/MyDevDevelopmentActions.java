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

    private List<List<String>> getObjectIdsChunks(MyDevTranscriptView transcripts) {
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

    private List<CompletableFuture<MyDevTrainingLocalView>> getChunkedTrainingLocalFutures(Integer cultureId, List<List<String>> objectIdsChunks) {
        return objectIdsChunks
                .stream()
                .filter(objectIdsChunk -> objectIdsChunk != null && !objectIdsChunk.isEmpty())
                .map(objectIdsChunk -> CompletableFuture.supplyAsync(() -> myDevApiClient.getTrainingLocalData(cultureId, objectIdsChunk)))
                .collect(Collectors.toList());
    }

    private List<CompletableFuture<MyDevTrainingView>> getChunkedTrainingFutures(List<List<String>> objectIdsChunks) {
        return objectIdsChunks
                .stream()
                .filter(objectIdsChunk -> objectIdsChunk != null && !objectIdsChunk.isEmpty())
                .map(objectIdsChunk -> CompletableFuture.supplyAsync(() -> myDevApiClient.getTrainingData(objectIdsChunk)))
                .collect(Collectors.toList());
    }

    @Override
    public List<TrainingItem> getDevelopmentActions(String uid, Integer year) {
        long startTimeMillis = System.currentTimeMillis();
        final MyDevUserView userView = myDevApiClient.getUserData(uid);
        if (!MyDevView.isEmpty(userView)) {
            final UserItem user = userView.getValue().get(0);
            if (user != null && user.getId() != null) {

                final MyDevTranscriptView transcriptData = myDevApiClient.getTranscriptData(user.getId(), year);
                final List<List<String>> objectIdsChunks = getObjectIdsChunks(transcriptData);

                final CompletableFuture<List<MyDevTrainingLocalView>> trainingLocalFutures =
                        sequence(getChunkedTrainingLocalFutures(user.getLanguageId(), objectIdsChunks));
                final CompletableFuture<List<MyDevTrainingView>> trainingFutures =
                        sequence(getChunkedTrainingFutures(objectIdsChunks));

                try {

                    final List<MyDevTrainingView> myDevTrainingViews = trainingFutures.get();
                    final List<MyDevTrainingLocalView> myDevTrainingLocalViews = trainingLocalFutures.get();

                    long endTimeMillis = System.currentTimeMillis();

                    System.out.println("The request took : " + ((endTimeMillis - startTimeMillis) / 1000));

                    return myDevTrainingViews
                            .stream()
                            .map(trainingView -> trainingView.getValue())
                            .flatMap(List::stream)
                            .collect(Collectors.toList());

                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
        return Collections.emptyList();
    }
}
