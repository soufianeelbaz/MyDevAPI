package com.ibm.mydev.personaldata.infrasctructure.mydev.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ibm.mydev.personaldata.domain.developmentactions.*;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.configuration.MyDevClientException;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.*;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ApiMyDevDevelopmentActions implements MyDevDevelopmentActions {

    private static Logger LOGGER = LoggerFactory
            .getLogger(MyDevDevelopmentActions.class);

    public static final String MY_DEV_EXTERNAL_TRAINING = "EXTL";
    public static final String INTERNAL_TRAININGS = "INTERNAL_TRAININGS";
    public static final String EXTERNAL_TRAININGS = "EXTERNAL_TRAININGS";
    public static final int START_INDEX_1_TO_EXCLUDE_CURRENT_YEAR = 1;

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

        try {
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
        }
        catch (IOException e) {
            // todo
        }
        catch(MyDevClientException e) {
            int code = e.getCode();
            String message = e.getMessage();
            LOGGER.error("Erreur code : {}, message: {}", code, message);
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
                })
                .filter(d->Objects.nonNull(d))
                .flatMap(developmentActions -> developmentActions.stream())
                .collect(Collectors.toSet());
    }

    private Set<DevelopmentAction> transformTranscriptToDevelopmentAction(UserItem user,
                                                                     TranscriptItem transcript,
                                                                     TrainingItem training,
                                                                     TrainingLocalItem trainingLocal,
                                                                     Map<String, DevelopmentActionType> translatedActionTypesMappedByCode) {

        Integer beginTrainingYear = getBeginTrainingYear(transcript, training);
        Integer completionTrainingYear = getCompletionTrainingYear(transcript);
        int currentYear = LocalDate.now().getYear();

        if (transcript.getStatus() != null && beginTrainingYear != null) {
            if (beginTrainingYear <= currentYear) {
                int endIndexForTrainingInProgress = currentYear - beginTrainingYear;
                switch (transcript.getStatus()) {
                    case NOT_STARTED:
                    case IN_PROGRESS:
                        if (beginTrainingYear.equals(currentYear)) {
                            return Sets.newHashSet(
                                    createDevelopmentAction(user, transcript, training, trainingLocal, translatedActionTypesMappedByCode,
                                            DevelopmentActionStatus.IN_PROGRESS, currentYear)
                            );
                        } else if (isIntervalOfYearsValid(beginTrainingYear, currentYear)) {
                            return prepareDevelopmentActionsInProgress(user, transcript, training, trainingLocal, translatedActionTypesMappedByCode,
                                    currentYear, START_INDEX_1_TO_EXCLUDE_CURRENT_YEAR - 1, endIndexForTrainingInProgress);
                        }
                        break;
                    case DONE:
                        if (completionTrainingYear != null && completionTrainingYear.equals(currentYear)) {
                            if (beginTrainingYear.equals(currentYear)) {
                                return Sets.newHashSet(
                                        createDevelopmentAction(user, transcript, training, trainingLocal, translatedActionTypesMappedByCode,
                                                DevelopmentActionStatus.DONE, currentYear)
                                );
                            } else if (isIntervalOfYearsValid(beginTrainingYear, currentYear)) {
                                Set<DevelopmentAction> developmentActions = prepareDevelopmentActionsInProgress(user, transcript, training, trainingLocal,
                                        translatedActionTypesMappedByCode, currentYear, START_INDEX_1_TO_EXCLUDE_CURRENT_YEAR, endIndexForTrainingInProgress);
                                developmentActions.add(
                                        createDevelopmentAction(user, transcript, training, trainingLocal, translatedActionTypesMappedByCode,
                                                DevelopmentActionStatus.DONE, currentYear)
                                );
                                return developmentActions;
                            }
                        }
                        break;
                    default:
                        return null;
                }
            } else if (isExpectedDevelopmentActionNextYear(transcript, beginTrainingYear, currentYear)) {
                return Sets.newHashSet(
                        createDevelopmentAction(user, transcript, training, trainingLocal, translatedActionTypesMappedByCode,
                                DevelopmentActionStatus.IN_PROGRESS, currentYear + 1)
                );
            }
        }

        return null;
    }

    private Set<DevelopmentAction> prepareDevelopmentActionsInProgress(UserItem user, TranscriptItem transcript, TrainingItem training, TrainingLocalItem trainingLocal,
                                                                       Map<String, DevelopmentActionType> translatedActionTypesMappedByCode, int currentYear, int startIndex, int endIndex) {
        Set<DevelopmentAction> developmentActions = Sets.newHashSet();
        IntStream.rangeClosed(startIndex, endIndex)
                .forEach(rangeValue -> {
                    developmentActions.add(
                            createDevelopmentAction(user, transcript, training, trainingLocal, translatedActionTypesMappedByCode, DevelopmentActionStatus.IN_PROGRESS, currentYear - rangeValue)
                    );
                });
        return developmentActions;
    }

    private DevelopmentAction createDevelopmentAction(UserItem user, TranscriptItem transcript, TrainingItem training, TrainingLocalItem trainingLocal,
                                                      Map<String, DevelopmentActionType> translatedActionTypesMappedByCode, DevelopmentActionStatus status, int year) {

        return new DevelopmentAction(
                user.getCollaborator(),
                user.getCollaborator(),
                new ArrayList<>(),
                getTrainingTitle(training, trainingLocal),
                manageDeadlineOfDevelopmentAction(transcript, training, status, year),
                year,
                status,
                getDevelopmentActionsType(training, translatedActionTypesMappedByCode),
                getMandatoryTraining(transcript),
                getCategory(training)
        );
    }

    private LocalDate manageDeadlineOfDevelopmentAction(TranscriptItem transcript, TrainingItem training, DevelopmentActionStatus status, int year) {
        if (status.equals(DevelopmentActionStatus.IN_PROGRESS)) {
            if (training.getEndDate() != null) {
                return ZonedDateTime.parse(training.getEndDate()).toLocalDate();
            } else if (transcript.getDueDate() != null) {
                return ZonedDateTime.parse(transcript.getDueDate()).toLocalDate();
            }
        } else if (status.equals(DevelopmentActionStatus.DONE) && transcript.getCompletionDate() != null) {
            return ZonedDateTime.parse(transcript.getCompletionDate()).toLocalDate();
        }
        return LocalDate.of(year, Month.DECEMBER, 1).with(TemporalAdjusters.lastDayOfYear());
    }

    private boolean isExpectedDevelopmentActionNextYear(TranscriptItem transcript, Integer beginTrainingYear, int currentYear) {
        return beginTrainingYear.equals(currentYear + 1) && transcript.getStatus().equals(NOT_STARTED);
    }

    private boolean isIntervalOfYearsValid(Integer beginTrainingYear, int currentYear) {
        return beginTrainingYear <= currentYear - 1 && beginTrainingYear >= currentYear - 3;
    }

    private Integer getBeginTrainingYear(TranscriptItem transcript, TrainingItem training) {
        if (training.getStartDate() != null) {
            return ZonedDateTime.parse(training.getStartDate()).getYear();
        } else if (transcript.getRegistrationDate() != null) {
            return ZonedDateTime.parse(transcript.getRegistrationDate()).getYear();
        }
        return null;
    }

    private Integer getCompletionTrainingYear(TranscriptItem transcript) {
        if (transcript.getCompletionDate() != null) {
            return ZonedDateTime.parse(transcript.getCompletionDate()).getYear();
        }
        return null;
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

    private List<CompletableFuture<MyDevTrainingLocalView>> getChunkedTrainingLocalFutures(Integer cultureId, List<List<String>> objectIdsChunks) throws IOException, MyDevClientException {
        return objectIdsChunks
                .stream()
                .filter(objectIdsChunk -> objectIdsChunk != null && !objectIdsChunk.isEmpty())
                .map(objectIdsChunk -> CompletableFuture.supplyAsync(() -> myDevApiClient.getTrainingLocalData(cultureId, objectIdsChunk)))
                .collect(Collectors.toList());
    }

    private List<CompletableFuture<MyDevTrainingView>> getChunkedTrainingFutures(List<List<String>> objectIdsChunks) throws IOException, MyDevClientException {
        return objectIdsChunks
                .stream()
                .filter(objectIdsChunk -> objectIdsChunk != null && !objectIdsChunk.isEmpty())
                .map(objectIdsChunk -> CompletableFuture.supplyAsync(() -> myDevApiClient.getTrainingData(objectIdsChunk)))
                .collect(Collectors.toList());
    }
}
