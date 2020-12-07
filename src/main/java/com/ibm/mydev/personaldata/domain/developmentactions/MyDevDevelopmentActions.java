package com.ibm.mydev.personaldata.domain.developmentactions;

import com.google.common.collect.Lists;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.MyDevApiClient;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
public class MyDevDevelopmentActions implements DevelopmentActions {

    private static Logger LOGGER = LoggerFactory
            .getLogger(MyDevDevelopmentActions.class);

    @Autowired
    public MyDevApiClient myDevApiClient;

    @Override
    public List<DevelopmentAction> getDevelopmentActions(String uid, String year) throws Exception {
        /**MyDevUserView userData = myDevApiClient.getUserData(uid);
        if (!MyDevView.isEmpty(userData)) {
            MyDevTranscriptView transcriptData = myDevApiClient.getTranscriptData(userData.getValue().get(0).getId(), Integer.parseInt(year));
            if (!MyDevView.isEmpty(transcriptData)) {
                List<String> objectIds = transcriptData.getValue()
                        .stream()
                        .map(transcriptItem -> transcriptItem.getTrainingId())
                        .filter(trainingId -> trainingId != null && !trainingId.isEmpty())
                        .collect(Collectors.toList());
                if (!objectIds.isEmpty()) {
                    MyDevTrainingView trainingData = CompletableFuture.supplyAsync(() ->
                            myDevApiClient.getTrainingData(objectIds)).get();
                    MyDevTrainingLocalView trainingLocalData = CompletableFuture.supplyAsync(() ->
                            myDevApiClient.getTrainingLocalData(userData.getValue().get(0).getLanguageId(), objectIds)).get();
                    return buildDevelopmentActions(userData, transcriptData, trainingData, trainingLocalData);
                }
            }
        }
        return Collections.emptyList();**/
        return null;
    }

    private CompletableFuture<MyDevTranscriptView> getUserTranscriptsAsync(MyDevUserView user, String year) {
        CompletableFuture<MyDevTranscriptView> userTranscripts = CompletableFuture.supplyAsync(
                () -> {
                    if (!MyDevView.isEmpty(user) && user.getValue().get(0).getId() != null) {
                        return myDevApiClient.getTranscriptData(user.getValue().get(0).getId(), Integer.parseInt(year));
                    } else {
                        return null;
                    }
                });
        return userTranscripts;
    }

    private CompletableFuture<MyDevTrainingView> getTrainingsAsync(List<List<String>> objectsIds) {
        CompletableFuture<MyDevTrainingView> trainings = CompletableFuture.supplyAsync(
                () -> {
                    return null;
                });
        return trainings;
    }

    private CompletableFuture<MyDevTrainingView> getTrainingLocalsAsync(List<List<String>> objectsIds) {
        CompletableFuture<MyDevTrainingView> trainingLocals = CompletableFuture.supplyAsync(
                () -> {
                    return null;
                });
        return trainingLocals;
    }

    private CompletableFuture<List<List<String>>> getObjectIdChunksAsync(MyDevTranscriptView transcripts) {
        return CompletableFuture.supplyAsync(() -> {
            if (!MyDevView.isEmpty(transcripts)) {
                List<String> objectIds = transcripts.getValue()
                        .stream()
                        .map(transcriptItem -> transcriptItem.getTrainingId())
                        .filter(trainingId -> trainingId != null && !trainingId.isEmpty())
                        .collect(Collectors.toList());
                return Lists.partition(objectIds, 10); // todo
            } else {
                return Collections.emptyList();
            }
        });
    }

    private List<List<String>> getObjectIdsChunks(MyDevTranscriptView transcripts) {
        List<String> objectIds = transcripts.getValue()
                .stream()
                .map(transcriptItem -> transcriptItem.getTrainingId())
                .filter(trainingId -> trainingId != null && !trainingId.isEmpty())
                .collect(Collectors.toList());
        return Lists.partition(objectIds, 10);
    }


    private CompletableFuture<List<DevelopmentAction>> getTrainingsAndTrainingLocalsCombined(List<List<String>> objectIdsChunks) {
        /**CompletableFuture.supplyAsync(() -> {
            List<CompletableFuture<?>> chunkedTrainingFutures = getChunkedTrainingFutures(objectIdsChunks);
            List<CompletableFuture<?>> chunkedTrainingLocalFutures = getChunkedTrainingLocalFutures(objectIdsChunks);
        });**/ // Droit au mur !
        return null;
    }

    private List<CompletableFuture<?>> getChunkedTrainingFutures(List<List<String>> objectIdsChunks) {
        return objectIdsChunks
                .stream()
                .filter(objectIdsChunk -> objectIdsChunk != null && !objectIdsChunk.isEmpty())
                .map(objectIdsChunk -> CompletableFuture.supplyAsync(() -> myDevApiClient.getTrainingData(objectIdsChunk)))
                .collect(Collectors.toList());
    }

    private List<CompletableFuture<?>> getChunkedTrainingLocalFutures(List<List<String>> objectIdsChunks) {
        return objectIdsChunks
                .stream()
                .filter(objectIdsChunk -> objectIdsChunk != null && !objectIdsChunk.isEmpty())
                .map(objectIdsChunk -> CompletableFuture.supplyAsync(() -> myDevApiClient.getTrainingLocalData(1, objectIdsChunk))) // todo
                .collect(Collectors.toList());
    }

    private List<DevelopmentAction> futures(String uid, String year) {
        CompletableFuture<List<DevelopmentAction>> developmentActions =
                CompletableFuture.supplyAsync(() -> myDevApiClient.getUserData(uid))
                        .thenCompose((user) -> getUserTranscriptsAsync(user, year))
                        .thenCompose((transcripts) -> getObjectIdChunksAsync(transcripts))
                        .thenCompose((objectIdChunks) -> getTrainingsAndTrainingLocalsCombined(objectIdChunks));
        return null;
    }

    private Observable<MyDevTranscriptView> getTranscriptsDataObservable(Integer userId, Integer year) {
        return Observable.just(myDevApiClient.getTranscriptData(userId, year));
    }

    private Observable<MyDevUserView> getUserDataObservable(String uid) {
        return Observable.just(myDevApiClient.getUserData(uid));
    }

    private Observable<List<List<String>>> getObjectIdsChunksObservable(List<TranscriptItem> transcriptItems) {
        List<String> objectIds = transcriptItems.stream()
                .filter(item -> item.getTrainingId() != null)
                .map(item -> item.getTrainingId())
                .collect(Collectors.toList());
        return Observable.just(Lists.partition(objectIds, 10));
    }

    private Observable<MyDevTrainingView> getTrainingsObservable(List<List<String>> objectIdsChunks) {

        return Observable.merge(
                objectIdsChunks
                        .stream()
                        .map(objectIdsChunk -> Observable.just(myDevApiClient.getTrainingData(objectIdsChunk)))
                        .collect(Collectors.toList()));
    }

    private Observable<MyDevTrainingLocalView> getTrainingLocalsObservable(List<List<String>> objectIdsChunks) {

        return Observable.merge(
                objectIdsChunks
                        .stream()
                        .map(objectIdsChunk -> Observable.just(myDevApiClient.getTrainingLocalData(1, objectIdsChunk))) // todo
                        .collect(Collectors.toList()));
    }

    private List<DevelopmentAction> mergeTrainingsAndTrainingLocals(MyDevTrainingView trainingView,
                                                                    MyDevTrainingLocalView trainingLocalView) {
        return Collections.emptyList();
    }

    @Override
    public  Observable<List<DevelopmentAction>> getDeveloppementActionsObservable(String uid, Integer year) {

        return getUserDataObservable(uid)
                .filter(userView -> !MyDevView.isEmpty(userView) && userView.getValue().get(0).getId() != null)
                .flatMap(userView -> Observable.just(userView.getValue().get(0)))
                .flatMap(userItem -> getTranscriptsDataObservable(userItem.getId(), year))
                .filter(transcriptsView -> !MyDevView.isEmpty(transcriptsView))
                .flatMap(transcriptView -> Observable.just(transcriptView.getValue()))
                .flatMap(transcripts -> getObjectIdsChunksObservable(transcripts))
                .flatMap(objectIdsChunks -> Observable.zip(
                        getTrainingsObservable(objectIdsChunks),
                        getTrainingLocalsObservable(objectIdsChunks),
                        (myDevTrainingView, myDevTrainingLocalView) -> mergeTrainingsAndTrainingLocals(myDevTrainingView, myDevTrainingLocalView)))
                .doOnNext(listObservable -> LOGGER.info("Done"))
                .doOnError(throwable -> LOGGER.error(throwable.getMessage()));
    }
}
