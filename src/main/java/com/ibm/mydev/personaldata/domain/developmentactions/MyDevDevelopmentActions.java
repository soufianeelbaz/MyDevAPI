package com.ibm.mydev.personaldata.domain.developmentactions;

import com.google.common.collect.Lists;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.IMyDevApiClient;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.configuration.MyDevApiConfiguration;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.MyDevApiClient;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
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
    public IMyDevApiClient myDevApiClient;

    @Value("${mydev.csod.api.chunk.url.size}")
    public int chunkUrlSize;

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
            return Lists.partition(objectIds, chunkUrlSize);
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
    public List<DevelopmentAction> getDevelopmentActions(String uid, Integer year) {

        final MyDevUserView userView = myDevApiClient.getUserData(uid);
        if (!MyDevView.isEmpty(userView)) {
            final UserItem user = userView.getValue().get(0);
            if (user != null && user.getId() != null) {

                final MyDevTranscriptView transcriptView = myDevApiClient.getTranscriptData(user.getId(), year);

                if (!MyDevView.isEmpty(transcriptView)) {

                    final List<TranscriptItem> transcripts = transcriptView.getValue();
                    final List<List<String>> objectIdsChunks = getObjectIdsChunks(transcriptView);

                    final CompletableFuture<List<MyDevTrainingLocalView>> trainingLocalFutures =
                            sequence(getChunkedTrainingLocalFutures(user.getLanguageId(), objectIdsChunks));
                    final CompletableFuture<List<MyDevTrainingView>> trainingFutures =
                            sequence(getChunkedTrainingFutures(objectIdsChunks));

                    List<MyDevTrainingView> myDevTrainingViews = null;
                    List<MyDevTrainingLocalView> myDevTrainingLocalViews = null;

                    try {
                        myDevTrainingViews = trainingFutures.get();
                        myDevTrainingLocalViews = trainingLocalFutures.get();
                    } catch (InterruptedException | ExecutionException e) {
                        LOGGER.error(e.getMessage());
                    }
                    if (myDevTrainingViews != null) {
                        final List<TrainingItem> trainings = myDevTrainingViews.stream().map(trainingView -> trainingView.getValue()).flatMap(List::stream).collect(Collectors.toList());
                        List<TrainingLocalItem> trainingLocals = new ArrayList<>();
                        if (myDevTrainingLocalViews != null) {
                            trainingLocals = myDevTrainingLocalViews.stream().map(trainingLocalView -> trainingLocalView.getValue()).flatMap(List::stream).collect(Collectors.toList());
                        }
                        return buildDevelopementActions(user, transcripts, trainings, trainingLocals);
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    private List<DevelopmentAction> buildDevelopementActions(UserItem user,
                                                              List<TranscriptItem> transcripts,
                                                              List<TrainingItem> trainings,
                                                              List<TrainingLocalItem> trainingLocals) {
        Map<String, TrainingItem> trainingItemMappedById = trainings
                .stream()
                .filter(trainingItem -> !StringUtils.isEmpty(trainingItem.getObjectId()))
                .collect(Collectors.toMap(TrainingItem::getObjectId, Function.identity()));

        Map<String, TrainingLocalItem> trainingLocalItemMappedById = trainingLocals
                .stream()
                .filter(trainingLocalItem ->
                        !StringUtils.isEmpty(trainingLocalItem.getObjectId()) &&
                        !StringUtils.isEmpty(trainingLocalItem.getTitle()))
                .collect(Collectors.toMap(TrainingLocalItem::getObjectId, Function.identity()));

        return transcripts
                .stream()
                .filter(transcriptItem -> !StringUtils.isEmpty(transcriptItem.getTrainingId()))
                .map(transcript -> {
                    TrainingItem training = trainingItemMappedById.get(transcript.getTrainingId());
                    TrainingLocalItem trainingLocal = trainingLocalItemMappedById.get(transcript.getTrainingId());
                    return transformTranscriptToDevelopmentAction(user, transcript, training, trainingLocal);
                }).collect(Collectors.toList());
    }

    private DevelopmentAction transformTranscriptToDevelopmentAction(UserItem user,
                                                                     TranscriptItem transcript,
                                                                     TrainingItem training,
                                                                     TrainingLocalItem trainingLocal) {
        DevelopmentAction developmentAction = new DevelopmentAction();
        developmentAction.setSource(user.getCollaborator());
        developmentAction.setSubject(user.getCollaborator());
        developmentAction.setActionName(trainingLocal != null ? trainingLocal.getTitle() : training.getTitle());
        developmentAction.setDeadline(LocalDate.now());
        developmentAction.setDevelopmentYear("2020");
        developmentAction.setDevelopmentActionStatus(getDevelopmentActionsStatus(transcript));
        developmentAction.setDevelopmentActionType(getDevelopmentActionsType(training));
        developmentAction.setDeleted(false);
        return developmentAction;
    }

    private DevelopmentActionType getDevelopmentActionsType(TrainingItem training) {
        DevelopmentActionType type = null;
        if (!StringUtils.isEmpty(training.getCategory())) {
            String category = training.getCategory();
            type = "EXTL".equals(category) ? new DevelopmentActionType("EXTERNAL_TRAINING") :
                    new DevelopmentActionType("INTERNAL_TRAINING");
        }
        return  type;
    }

    private DevelopmentActionStatus getDevelopmentActionsStatus(TranscriptItem transcript) {
        DevelopmentActionStatus status = null;
        if (transcript.getStatus() != null) {
            Integer myDevStatus = transcript.getStatus();
            switch(myDevStatus) {
                case 12:
                case 13:
                    status = DevelopmentActionStatus.IN_PROGRESS;
                    break;
                case 11:
                    status = DevelopmentActionStatus.DONE;
                    break;
                default:
                    break;
            }
        }
        return status;
    }
}
