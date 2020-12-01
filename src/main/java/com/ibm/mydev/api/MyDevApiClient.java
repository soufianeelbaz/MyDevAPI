package com.ibm.mydev.api;

import com.ibm.mydev.api.configuration.MyDevApiConfiguration;
import com.ibm.mydev.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

import com.ibm.mydev.dto.UserReportViewItemDTO.UserReportAttributes;
import com.ibm.mydev.dto.TranscriptReportViewItemDTO.TranscriptReportAttributes;
import com.ibm.mydev.dto.TrainingReportViewItemDTO.TrainingReportAttributes;
import com.ibm.mydev.dto.TrainingLocalViewItemDTO.TrainingLocalReportAttributes;

@Service
public class MyDevApiClient {

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

    @Autowired
    public MyDevApiConfiguration apiConfiguration;

    @Autowired
    @Qualifier("MyDevRestTemplate")
    public RestTemplate restTemplate;

    public <T> ResponseEntity<T> query(String url, HttpEntity request, Class<T> clazz) {
        return restTemplate.exchange(url, HttpMethod.GET, request, clazz);
    }

    public ResponseEntity<UserReportViewPayloadDTO> getUserData(String uid) throws Exception {
        HttpHeaders headers = getHeaders();
        HttpEntity request = new HttpEntity(headers);
        String urlWithParams = getUsersEndpointUri(uid);
        return query(urlWithParams, request, UserReportViewPayloadDTO.class);
    }

    public ResponseEntity<TranscriptReportViewPayloadDTO> getTranscriptData(Long id, Integer year) throws Exception {
        HttpHeaders headers = getHeaders();
        HttpEntity request = new HttpEntity(headers);
        String urlWithParams = getTranscriptsEndpointUri(id, year);
        return query(urlWithParams, request, TranscriptReportViewPayloadDTO.class);
    }

    public ResponseEntity<TrainingReportViewPayloadDTO> getTrainingData(String objectId) throws Exception {
        HttpHeaders headers = getHeaders();
        HttpEntity request = new HttpEntity(headers);
        String urlWithParams = getTrainingsEndpointUri(objectId);
        return query(urlWithParams, request, TrainingReportViewPayloadDTO.class);
    }

    public ResponseEntity<TrainingLocalViewPayloadDTO> getTrainingLocalData(Long cultureId, List<String> objectIds) throws Exception {
        HttpHeaders headers = getHeaders();
        HttpEntity request = new HttpEntity(headers);
        String urlWithParams = getTrainingsLocalEndpointUri(cultureId, objectIds);
        return query(urlWithParams, request, TrainingLocalViewPayloadDTO.class);
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
        sb.append(UserReportViewItemDTO.UserReportAttributes.USER_REFERENCE.getAttribute());
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
    }

    private String getTrainingsEndpointUri(String objectId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiConfiguration.baseUrl + apiConfiguration.trainings)
                .queryParam(PARAM_FILTER, buildTrainingFilterParams(objectId))
                .queryParam(PARAM_SELECT, Arrays.stream(TrainingReportAttributes.values())
                        .map(attr -> attr.getAttribute())
                        .collect(Collectors.joining(",")));
        return builder.toUriString();
    }

    private String buildTrainingFilterParams(String objectId) {
        StringBuilder sb = new StringBuilder();
        sb.append(TrainingReportAttributes.OBJECT_ID.getAttribute());
        sb.append(FILTER_EQUAL);
        sb.append(objectId);
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




















