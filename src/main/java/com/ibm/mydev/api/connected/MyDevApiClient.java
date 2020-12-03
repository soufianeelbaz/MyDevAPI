package com.ibm.mydev.api.connected;

import com.google.common.collect.Lists;
import com.ibm.mydev.api.IMyDevApiClient;
import com.ibm.mydev.api.configuration.MyDevApiConfiguration;
import com.ibm.mydev.dto.*;
import com.ibm.mydev.dto.TrainingItem.TrainingReportAttributes;
import com.ibm.mydev.dto.TrainingLocalItem.TrainingLocalReportAttributes;
import com.ibm.mydev.dto.TranscriptItem.TranscriptReportAttributes;
import com.ibm.mydev.dto.UserItem.UserReportAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Profile("!MYDEV_MOCK")
@Service
public class MyDevApiClient implements IMyDevApiClient {

    public static final String AUTHORIZATION_KEY = "Authorization";

    private static final String PARAM_FILTER = "$filter";
    private static final String PARAM_SELECT = "$select";

    private static final String SINGLE_QUOTE = "'";

    private static final String FILTER_EQUAL = " eq ";
    private static final String FILTER_TRANSCRIPT_QUERY = "transc_user_id eq $TRANSC_USER_ID " +
            "and is_removed eq false " +
            "and is_standalone eq true " +
            "and (((user_lo_status_group_id eq 12 or user_lo_status_group_id eq 13) and is_archive eq 0) " +
            "or (user_lo_status_group_id eq 11 and user_lo_comp_dt ge cast('$YEAR', Edm.DateTimeOffset)))";

    private static final String OPERATOR_AND = " and ";
    private static final String OPERATOR_OR = " or ";

    private static final String OPENING_PARENTHESE = "(";
    private static final String CLOSING_PARENTHESE = ")";

    private static final int PARTITION_SIZE = 20;

    @Autowired
    public MyDevApiConfiguration apiConfiguration;

    @Autowired
    @Qualifier("MyDevRestTemplate")
    public RestTemplate restTemplate;

    public <T> T query(String url, HttpEntity request, Class<T> clazz) {
        return restTemplate.exchange(url, HttpMethod.GET, request, clazz).getBody();
    }

    @Override
    public MyDevUserView getUserData(String uid) {
        HttpHeaders headers = getHeaders();
        HttpEntity request = new HttpEntity(headers);
        String urlWithParams = getUsersEndpointUri(uid);
        // MyDevUser todo
        return query(urlWithParams, request, MyDevUserView.class);
    }

    @Override
    public MyDevTranscriptView getTranscriptData(Long id, Integer year) {
        HttpHeaders headers = getHeaders();
        HttpEntity request = new HttpEntity(headers);
        String urlWithParams = getTranscriptsEndpointUri(id, year);
        MyDevTranscriptView view = query(urlWithParams, request, MyDevTranscriptView.class);
        return view;
    }

    @Override
    public MyDevTrainingView getTrainingData(List<String> objectIds) {
        HttpHeaders headers = getHeaders();
        HttpEntity request = new HttpEntity(headers);

        List<List<String>> partObjectIds = Lists.partition(objectIds, PARTITION_SIZE);
        MyDevTrainingView myDevTrainingView = new MyDevTrainingView();
        partObjectIds.forEach(objIds -> {
            String urlWithParams = getTrainingsEndpointUri(objIds);
            MyDevTrainingView myDevTrainingViewTemp = query(urlWithParams, request, MyDevTrainingView.class);
            myDevTrainingView.setContext(myDevTrainingViewTemp.getContext());
            myDevTrainingView.getValue().addAll(myDevTrainingViewTemp.getValue());
        });

        return myDevTrainingView;
    }

    @Override
    public MyDevTrainingLocalView getTrainingLocalData(Long cultureId, List<String> objectIds) {
        HttpHeaders headers = getHeaders();
        HttpEntity request = new HttpEntity(headers);

        List<List<String>> partObjectIds = Lists.partition(objectIds, PARTITION_SIZE);
        MyDevTrainingLocalView myDevTrainingLocalView = new MyDevTrainingLocalView();
        partObjectIds.forEach(objIds -> {
            String urlWithParams = getTrainingsLocalEndpointUri(cultureId, objIds);
            MyDevTrainingLocalView myDevTrainingLocalViewTemp = query(urlWithParams, request, MyDevTrainingLocalView.class);
            myDevTrainingLocalView.setContext(myDevTrainingLocalViewTemp.getContext());
            myDevTrainingLocalView.getValue().addAll(myDevTrainingLocalViewTemp.getValue());
        });

        return myDevTrainingLocalView;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, "application/json");
        headers.set(AUTHORIZATION_KEY, "Bearer WRONG_TOKEN");
        return headers;
    }

    private String getUsersEndpointUri(String uid) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiConfiguration.baseUrl + apiConfiguration.users)
                .queryParam(PARAM_FILTER, buildUserFilterParams(uid))
                .queryParam(PARAM_SELECT, Arrays.stream(UserReportAttributes.values())
                        .map(attr -> attr.getAttribute())
                        .collect(Collectors.joining(",")));
        return builder.toUriString();
    }

    private String buildUserFilterParams(String uid) {
        StringBuilder sb = new StringBuilder();
        sb.append(UserItem.UserReportAttributes.USER_REFERENCE.getAttribute());
        sb.append(FILTER_EQUAL);
        sb.append(SINGLE_QUOTE);
        sb.append(uid);
        sb.append(SINGLE_QUOTE);
        return sb.toString();
    }

    private String getTranscriptsEndpointUri(Long id, Integer year) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiConfiguration.baseUrl + apiConfiguration.transcripts)
                .queryParam(PARAM_FILTER, buildTranscriptFilterParams(id, year))
                .queryParam(PARAM_SELECT, Arrays.stream(TranscriptReportAttributes.values())
                        .map(attr -> attr.getAttribute())
                        .collect(Collectors.joining(",")));
        return builder.toUriString();
    }

    private String buildTranscriptFilterParams(Long transcriptUserId, Integer year) {
        return FILTER_TRANSCRIPT_QUERY
                .replace("$TRANSC_USER_ID", String.valueOf(transcriptUserId))
                .replace("$YEAR", (year - 5) + "-01-01");
        // "%s" todo
    }

    private String getTrainingsEndpointUri(List<String> objectIds) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiConfiguration.baseUrl + apiConfiguration.trainings)
                .queryParam(PARAM_FILTER, buildTrainingFilterParams(objectIds))
                .queryParam(PARAM_SELECT, Arrays.stream(TrainingReportAttributes.values())
                        .map(attr -> attr.getAttribute())
                        .collect(Collectors.joining(",")));
        return builder.toUriString();
    }

    private String buildTrainingFilterParams(List<String> objectIds) {
        StringBuilder sb = new StringBuilder();
        sb.append(OPENING_PARENTHESE);
        sb.append(objectIds.stream().map(id -> TrainingReportAttributes.OBJECT_ID.getAttribute() + FILTER_EQUAL + id)
                .collect(Collectors.joining(OPERATOR_OR)));
        sb.append(CLOSING_PARENTHESE);
        return sb.toString();
    }

    private String getTrainingsLocalEndpointUri(Long cultureId, List<String> objectIds) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiConfiguration.baseUrl + apiConfiguration.trainingsLocal)
                .queryParam(PARAM_FILTER, buildTrainingLocalFilterParams(cultureId, objectIds));
        return builder.toUriString();
    }

    private String buildTrainingLocalFilterParams(Long cultureId, List<String> objectIds) {
        StringBuilder sb = new StringBuilder();
        sb.append(TrainingLocalReportAttributes.TRAINING_TITLE_LOCAL_CULTURE_ID.getAttribute());
        sb.append(FILTER_EQUAL);
        sb.append(cultureId);
        sb.append(OPERATOR_AND);
        sb.append(buildInObjectIdsFilter(objectIds));
        return sb.toString();
    }

    private String buildInObjectIdsFilter(List<String> objectIds) {
        String filters = objectIds.stream().map(objectId -> TrainingLocalReportAttributes.TRAINING_TITLE_LOCAL_OBJECT_ID.getAttribute() + FILTER_EQUAL + objectId).collect(Collectors.joining(OPERATOR_OR));
        return OPENING_PARENTHESE + filters + CLOSING_PARENTHESE;
    }
}




















