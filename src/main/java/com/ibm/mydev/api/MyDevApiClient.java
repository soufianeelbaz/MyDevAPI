package com.ibm.mydev.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.mydev.config.MyDevConfiguration;
import com.ibm.mydev.dto.TranscriptReportViewPayloadDTO;
import com.ibm.mydev.dto.UserReportViewPayloadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MyDevApiClient {

    private static final String PARAM_FILTER = "$filter";
    private static final String PARAM_SELECT = "$select";

    private static final String SINGLE_QUOTE = "'";

    private static final String FILTER_EQUAL = " eq ";
    private static final String FILTER_GREATER_THAN_OR_EQUAL = " ge ";
    private static final String FILTER_LESS_THAN = " lt ";
    private static final String AND = " and ";

    @Autowired
    public MyDevConfiguration apiConfiguration;

    @Autowired
    @Qualifier("baseUrl")
    public String baseUrl;

    @Autowired
    @Qualifier("authenticationUrl")
    public URL authenticationUrl;

    private static final String ACCEPT = "Accept";
    private static final String AUTHORIZATION_KEY = "Authorization";
    private static final String PREFER = "prefer";

    private static final String SCOPE = "all";
    private static final String GRANT_TYPE = "client_credentials";
    private static final String SCOPE_KEY = "scope";
    private static final String GRANT_TYPE_KEY = "grantType";
    private static final String CLIENT_ID_KEY = "clientId";
    private static final String CLIENT_SECRET_KEY = "clientSecret";


    private HttpURLConnection getPayloadHttpConnection(URL url) throws Exception {
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setReadTimeout(0);
        httpConnection.setUseCaches(false);
        httpConnection.setRequestMethod("GET");
        httpConnection.setRequestProperty(HttpHeaders.ACCEPT, "application/json");
        httpConnection.setDoOutput(false);
        httpConnection.setDoInput(true);
        addHeadersToRequest(getAuthRequestHeaders(), httpConnection);
        return httpConnection;
    }

    public <T> ResponseEntity<T> query(String url, HttpEntity request, Class<T> clazz) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, request, clazz);
        return response;
    }

    public ResponseEntity<UserReportViewPayloadDTO> getUserData(String uid) throws Exception {
        String accessToken = getAccessToken();
        HttpHeaders headers = getHeaders(accessToken);
        HttpEntity request = new HttpEntity(headers);
        String urlWithParams = getUsersEndpointUri(uid);
        return query(urlWithParams, request, UserReportViewPayloadDTO.class);
    }

    public ResponseEntity<TranscriptReportViewPayloadDTO> getTranscriptData(Long id, Integer year) throws Exception {
        String accessToken = getAccessToken();
        HttpHeaders headers = getHeaders(accessToken);
        HttpEntity request = new HttpEntity(headers);
        String urlWithParams = getTranscriptsEndpointUri(id, year);
        return query(urlWithParams, request, TranscriptReportViewPayloadDTO.class);
    }

    private HttpHeaders getHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, "application/json");
        headers.set(AUTHORIZATION_KEY, getAuthorizationHeaderValue(accessToken));
        return headers;
    }

    private String getUsersEndpointUri(String uid) {
        String url = apiConfiguration.baseUrl + apiConfiguration.users;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam(PARAM_FILTER, buildUserFilterParams(uid))
                .queryParam(PARAM_SELECT, Arrays.stream(UserReportAttributes.values())
                        .map(attr -> attr.getAttribute())
                        .collect(Collectors.joining(",")));
        return builder.toUriString();
    }

    private String buildUserFilterParams(String uid) {
        StringBuilder sb = new StringBuilder();
        sb.append(UserReportAttributes.USER_REFERENCE.getAttribute());
        sb.append(FILTER_EQUAL);
        sb.append(SINGLE_QUOTE);
        sb.append(uid);
        sb.append(SINGLE_QUOTE);
        return sb.toString();
    }

    private String getTranscriptsEndpointUri(Long id, Integer year) {
        String url = apiConfiguration.baseUrl + apiConfiguration.transcripts;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam(PARAM_FILTER, buildTranscriptFilterParams(id, year))
                .queryParam(PARAM_SELECT, Arrays.stream(TranscriptReportAttributes.values())
                        .map(attr -> attr.getAttribute())
                        .collect(Collectors.joining(",")));
        return builder.toUriString();
    }

    private String buildTranscriptFilterParams(Long id, Integer year) {
        StringBuilder sb = new StringBuilder();
        sb.append(TranscriptReportAttributes.USER_LO_ASSIGNED_DT.getAttribute());
        sb.append(FILTER_GREATER_THAN_OR_EQUAL);
        sb.append("cast('" + year + "-01-01', Edm.DateTimeOffset)");
        sb.append(AND);
        sb.append(TranscriptReportAttributes.USER_LO_ASSIGNED_DT.getAttribute());
        sb.append(FILTER_LESS_THAN);
        sb.append("cast('" + (year + 1) + "-01-01', Edm.DateTimeOffset)");
        sb.append(AND);
        sb.append(TranscriptReportAttributes.TRANSC_USER_ID.getAttribute());
        sb.append(FILTER_EQUAL);
        sb.append(id);
        return sb.toString();
    }
    private List<Header> getAuthRequestHeaders() {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new Header("cache-control", "no-cache"));
        return headers;
    }

    private List<RequestParameter> getAuthenticationRequestParams() {
        List<RequestParameter> requestParameters = new ArrayList<RequestParameter>();
        requestParameters.add(new RequestParameter(CLIENT_ID_KEY, apiConfiguration.clientId));
        requestParameters.add(new RequestParameter(CLIENT_SECRET_KEY, apiConfiguration.password));
        requestParameters.add(new RequestParameter(GRANT_TYPE_KEY, GRANT_TYPE));
        requestParameters.add(new RequestParameter(SCOPE_KEY, SCOPE));
        return requestParameters;
    }

    private HttpURLConnection getAuthenticationHttpConnection() throws Exception {
        HttpURLConnection httpConnection = (HttpURLConnection) authenticationUrl.openConnection();
        httpConnection.setUseCaches(false);
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty(HttpHeaders.ACCEPT, "application/json");
        httpConnection.setDoOutput(true);
        httpConnection.setDoInput(true);
        addHeadersToRequest(getAuthRequestHeaders(), httpConnection);
        return httpConnection;
    }

    public synchronized String getAccessToken() throws Exception {
        HttpURLConnection connection = getAuthenticationHttpConnection();
        OutputStream outputStream = connection.getOutputStream();
        String requestBody = buildRequestBody(getAuthenticationRequestParams());
        outputStream.write(requestBody.getBytes());
        outputStream.flush();
        outputStream.close();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            String content = readConnectionInput(connection);
            JsonObject contentObject = new JsonParser().parse(content).getAsJsonObject();
            return contentObject.get("access_token").getAsString();
        }
        throw new Exception(String.format("Response by URL '%s' is '%s' '%s'.", null, connection.getResponseCode(),
                connection.getResponseMessage()));
    }

    private static String readConnectionInput(HttpURLConnection httpConnection) throws IOException {
        try (InputStreamReader inputStream = new InputStreamReader(httpConnection.getInputStream())) {
            BufferedReader reader = new BufferedReader(inputStream);

            String responseString;
            StringWriter writer = new StringWriter();
            while ((responseString = reader.readLine()) != null) {
                writer.write(responseString);
            }

            return writer.toString();
        }
    }

    private  String getAuthorizationHeaderValue(String accessToken) {
        return "Bearer " + accessToken;
    }

    private void addHeadersToRequest(List<Header> headers, HttpURLConnection httpConnection) {
        for (Header header : headers) {
            httpConnection.setRequestProperty(header.name, header.value);
        }
    }

    private String buildRequestBody(List<RequestParameter> requestParameters) {
        StringBuilder reqbody =new StringBuilder();
        Integer max=0;
        reqbody.append("{");
        for (RequestParameter requestParameter : requestParameters) {
            reqbody.append('"');
            reqbody.append(requestParameter.name);
            reqbody.append('"');
            reqbody.append(':');
            reqbody.append('"');
            reqbody.append(requestParameter.value);
            reqbody.append('"');
            if(max < requestParameters.size()-1)
            {
                reqbody.append(",");
            }
            max++;
        }
        reqbody.append("}");
        return reqbody.toString();
    }

}
enum UserReportAttributes {

    USER_REFERENCE("user_ref"),
    USER_ID("user_id"),
    USER_LANGUAGE_ID("user_language_id");

    private String attribute;

    UserReportAttributes(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }
}

enum TranscriptReportAttributes {

    TRANSC_OBJECT_ID("transc_object_id"),
    USER_LO_STATUS_GROUP_ID("user_lo_status_group_id"),
    IS_LATEST_REG_NUM("is_latest_reg_num"),
    IS_ARCHIVE("is_archive"),
    IS_REMOVED("is_removed"),
    IS_STANDALONE("is_standalone"),
    USER_LO_REG_DT("user_lo_reg_dt"),
    USER_LO_MIN_DUE_DATE("user_lo_min_due_date"),
    USER_LO_COMP_DT("user_lo_comp_dt"),
    TRAINING_PURPOSE("training_purpose"),
    TRANSC_USER_ID("transc_user_id"),
    USER_LO_ASSIGNED_DT("user_lo_assigned_dt");

    private String attribute;

    TranscriptReportAttributes(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }
}


class Header {
    public String name;
    public String value;

    public Header(String name, String value) {
        this.name = name;
        this.value = value;
    }
}

class RequestParameter {
    public String name;
    public String value;

    public RequestParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }
}

