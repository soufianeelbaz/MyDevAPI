package com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected;

import com.ibm.mydev.personaldata.infrasctructure.mydev.api.IMyDevApiClient;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.connected.token.MyDevTokenService;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

    private static Logger LOGGER = LoggerFactory
            .getLogger(MyDevApiClient.class);

    public static final String AUTHORIZATION_KEY = "Authorization";

    private static final String PARAM_FILTER = "$filter";
    private static final String PARAM_SELECT = "$select";

    private static final String SINGLE_QUOTE = "'";

    private static final String FILTER_EQUAL = " eq ";
    private static final String FILTER_TRANSCRIPT_QUERY = "transc_user_id eq $TRANSC_USER_ID " +
            "and is_removed eq false " +
            "and is_standalone eq true " +
            "and is_latest_reg_num eq 1 " +
            "and (((user_lo_status_group_id eq 12 or user_lo_status_group_id eq 13) and is_archive eq 0) " +
            "or (user_lo_status_group_id eq 11 and user_lo_comp_dt ge cast('$YEAR', Edm.DateTimeOffset)))";

    private static final String OPERATOR_AND = " and ";
    private static final String OPERATOR_OR = " or ";

    private static final String OPENING_PARENTHESE = "(";
    private static final String CLOSING_PARENTHESE = ")";

    @Value("${mydev.csod.api}")
    public String baseUrl;

    @Value("${mydev.csod.api.endpoints.trainings}")
    public String trainings;

    @Value("${mydev.csod.api.endpoints.trainingsLocal}")
    public String trainingsLocal;

    @Value("${mydev.csod.api.endpoints.transcripts}")
    public String transcripts;

    @Value("${mydev.csod.api.endpoints.users}")
    public String users;

    @Autowired
    @Qualifier("MyDevRestTemplate")
    public RestTemplate restTemplate;

    @Autowired
    public MyDevTokenService tokenService;

    public <T> T query(String url, HttpEntity request, Class<T> clazz) {
        return restTemplate.exchange(url, HttpMethod.GET, request, clazz).getBody();
    }

    @Override
    public MyDevUserView getUserData(String uid) {
        LOGGER.info("Récupération des données utilisateur \"{}\".", uid);
        HttpHeaders headers = getHeaders();
        HttpEntity request = new HttpEntity(headers);
        String urlWithParams = getUsersEndpointUri(uid);
        return query(urlWithParams, request, MyDevUserView.class);
    }

    @Override
    public MyDevTranscriptView getTranscriptData(Integer id, Integer year) {
        LOGGER.info("Récupération des données transcript de l'utilisateur {} depuis l'année {}.", id, year);
        HttpHeaders headers = getHeaders();
        HttpEntity request = new HttpEntity(headers);
        String urlWithParams = getTranscriptsEndpointUri(id, year);
        MyDevTranscriptView view = query(urlWithParams, request, MyDevTranscriptView.class);
        return view;
    }

    @Override
    public MyDevTrainingView getTrainingData(List<String> objectIds) {
        LOGGER.info("Récupération de la liste des formations {}.", objectIds);
        HttpHeaders headers = getHeaders();
        HttpEntity request = new HttpEntity(headers);
        String urlWithParams = getTrainingsEndpointUri(objectIds);
        return query(urlWithParams, request, MyDevTrainingView.class);
    }

    @Override
    public MyDevTrainingLocalView getTrainingLocalData(Integer cultureId, List<String> objectIds) {
        LOGGER.info("Récupération des traductions pour les formations {}.", objectIds);
        HttpHeaders headers = getHeaders();
        HttpEntity request = new HttpEntity(headers);
        String urlWithParams = getTrainingsLocalEndpointUri(cultureId, objectIds);
        return query(urlWithParams, request, MyDevTrainingLocalView.class);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, "application/json");
        headers.set(AUTHORIZATION_KEY, "Bearer " + tokenService.getAccessToken());
        return headers;
    }

    private String getUsersEndpointUri(String uid) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + users)
                .queryParam(PARAM_FILTER, buildUserFilterParams(uid))
                .queryParam(PARAM_SELECT, Arrays.stream(UserItem.UserReportAttributes.values())
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

    private String getTranscriptsEndpointUri(Integer id, Integer year) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + transcripts)
                .queryParam(PARAM_FILTER, buildTranscriptFilterParams(id, year))
                .queryParam(PARAM_SELECT, Arrays.stream(TranscriptItem.TranscriptReportAttributes.values())
                        .map(attr -> attr.getAttribute())
                        .collect(Collectors.joining(",")));
        return builder.toUriString();
    }

    private String buildTranscriptFilterParams(Integer transcriptUserId, Integer year) {
        return FILTER_TRANSCRIPT_QUERY
                .replace("$TRANSC_USER_ID", String.valueOf(transcriptUserId))
                .replace("$YEAR", (year - 5) + "-01-01");
        // "%s" todo
    }

    private String getTrainingsEndpointUri(List<String> objectIds) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + trainings)
                .queryParam(PARAM_FILTER, buildTrainingFilterParams(objectIds))
                .queryParam(PARAM_SELECT, Arrays.stream(TrainingItem.TrainingReportAttributes.values())
                        .map(attr -> attr.getAttribute())
                        .collect(Collectors.joining(",")));
        return builder.toUriString();
    }

    private String buildTrainingFilterParams(List<String> objectIds) {
        StringBuilder sb = new StringBuilder();
        sb.append(OPENING_PARENTHESE);
        sb.append(objectIds.stream().map(id -> TrainingItem.TrainingReportAttributes.OBJECT_ID.getAttribute() + FILTER_EQUAL + id)
                .collect(Collectors.joining(OPERATOR_OR)));
        sb.append(CLOSING_PARENTHESE);
        return sb.toString();
    }

    private String getTrainingsLocalEndpointUri(Integer cultureId, List<String> objectIds) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + trainingsLocal)
                .queryParam(PARAM_FILTER, buildTrainingLocalFilterParams(cultureId, objectIds));
        return builder.toUriString();
    }

    private String buildTrainingLocalFilterParams(Integer cultureId, List<String> objectIds) {
        StringBuilder sb = new StringBuilder();
        sb.append(TrainingLocalItem.TrainingLocalReportAttributes.TRAINING_TITLE_LOCAL_CULTURE_ID.getAttribute());
        sb.append(FILTER_EQUAL);
        sb.append(cultureId);
        sb.append(OPERATOR_AND);
        sb.append(buildInObjectIdsFilter(objectIds));
        return sb.toString();
    }

    private String buildInObjectIdsFilter(List<String> objectIds) {
        String filters = objectIds.stream().map(objectId -> TrainingLocalItem.TrainingLocalReportAttributes.TRAINING_TITLE_LOCAL_OBJECT_ID.getAttribute() + FILTER_EQUAL + objectId).collect(Collectors.joining(OPERATOR_OR));
        return OPENING_PARENTHESE + filters + CLOSING_PARENTHESE;
    }
}