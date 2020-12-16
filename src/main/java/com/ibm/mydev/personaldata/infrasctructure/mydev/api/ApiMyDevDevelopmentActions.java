package com.ibm.mydev.personaldata.infrasctructure.mydev.api;

import com.google.common.collect.Lists;
import com.ibm.mydev.personaldata.domain.developmentactions.*;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.*;
import io.swagger.models.auth.In;
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
public class ApiMyDevDevelopmentActions implements MyDevDevelopmentActions {

    private static Logger LOGGER = LoggerFactory
            .getLogger(MyDevDevelopmentActions.class);

    public static final String MY_DEV_EXTERNAL_TRAINING = "EXTL";
    public static final String INTERNAL_TRAININGS = "INTERNAL_TRAININGS";
    public static final String EXTERNAL_TRAININGS = "EXTERNAL_TRAININGS";

    private static final int DONE = 11;
    private static final int IN_PROGRESS = 12;
    private static final int NOT_STARTED = 13;

    @Autowired
    public IMyDevApiClient myDevApiClient;

    @Autowired
    private DevelopmentActionTypes developmentActionTypes;

    @Value("${mydev.csod.api.chunk.url.size}")
    private int chunkUrlSize;

    @Override
    public Set<DevelopmentAction> findBySubjectAndDevelopmentYear(String subject, int developmentYear, String locale) {

        final MyDevUserView userView = myDevApiClient.getUserData(subject);
        if (!MyDevView.isEmpty(userView)) {
            final UserItem user = userView.getValue().get(0);
            if (user != null && user.getId() != null) {

                final MyDevTranscriptView transcriptView = myDevApiClient.getTranscriptData(user.getId(), developmentYear);

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
                        return buildDevelopementActions(user, transcripts, trainings, trainingLocals, locale);
                    }
                }
            }
        }
        return Collections.emptySet();
    }

    private Set<DevelopmentAction> buildDevelopementActions(UserItem user,
                                                            List<TranscriptItem> transcripts,
                                                            List<TrainingItem> trainings,
                                                            List<TrainingLocalItem> trainingLocals,
                                                            String locale) {
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

        Map<String, DevelopmentActionType> translatedActionTypesMappedByCode =
                developmentActionTypes.findAll(locale)
                        .stream()
                        .filter(developmentActionType ->
                                developmentActionType.getCode() != null &&
                                        (developmentActionType.getCode().equals(EXTERNAL_TRAININGS) ||
                                                developmentActionType.getCode().equals(INTERNAL_TRAININGS)))
                        .collect(Collectors.toMap(d -> d.getCode(), Function.identity()));

        return transcripts
                .stream()
                .filter(transcriptItem -> !StringUtils.isEmpty(transcriptItem.getTrainingId()))
                .map(transcript -> {
                    TrainingItem training = trainingItemMappedById.get(transcript.getTrainingId());
                    TrainingLocalItem trainingLocal = trainingLocalItemMappedById.get(transcript.getTrainingId());
                    return transformTranscriptToDevelopmentAction(user, transcript, training, trainingLocal, translatedActionTypesMappedByCode);
                }).collect(Collectors.toSet());
    }

    private DevelopmentAction transformTranscriptToDevelopmentAction(UserItem user,
                                                                     TranscriptItem transcript,
                                                                     TrainingItem training,
                                                                     TrainingLocalItem trainingLocal,
                                                                     Map<String, DevelopmentActionType> translatedActionTypesMappedByCode) {
        return new DevelopmentAction(
                user.getCollaborator(),
                user.getCollaborator(),
                new ArrayList<>(),
                getTrainingTitle(training, trainingLocal),
                LocalDate.now(),
                2020,
                getDevelopmentActionsStatus(transcript),
                getDevelopmentActionsType(training, translatedActionTypesMappedByCode),
                getMandatoryTraining(transcript),
                getCategory(training));
    }

    private String getCategory(TrainingItem training) {
        if (training != null) {
            return training.getCategory();
        }
        return null;
    }


    private Boolean getMandatoryTraining(TranscriptItem transcript) {
        if(transcript != null && transcript.getTrainingPurpose() != null && transcript.getTrainingPurpose().equals(21)) {
            return true;
        }
        return false;
    }

    private String getTrainingTitle(TrainingItem training, TrainingLocalItem trainingLocal) {
        String title = null;
        if (trainingLocal != null) {
            title = trainingLocal.getTitle();
        } else if (training != null) {
            title = training.getTitle();
        }
        return title;
    }


    private DevelopmentActionType getDevelopmentActionsType(TrainingItem training, Map<String, DevelopmentActionType> translatedActionTypesMappedByCode) {
        if (training != null && !StringUtils.isEmpty(training.getCategory()) && MY_DEV_EXTERNAL_TRAINING.equals(training.getCategory())) {
            return translatedActionTypesMappedByCode.get(EXTERNAL_TRAININGS);
        }
        return translatedActionTypesMappedByCode.get(INTERNAL_TRAININGS);

    }

    private DevelopmentActionStatus getDevelopmentActionsStatus(TranscriptItem transcript) {
        DevelopmentActionStatus status = null;
        if (transcript.getStatus() != null) {
            Integer myDevStatus = transcript.getStatus();
            switch(myDevStatus) {
                case NOT_STARTED:
                case IN_PROGRESS:
                    status = DevelopmentActionStatus.IN_PROGRESS;
                    break;
                case DONE:
                    status = DevelopmentActionStatus.DONE;
                    break;
                default:
                    break;
            }
        }
        return status;
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
}
