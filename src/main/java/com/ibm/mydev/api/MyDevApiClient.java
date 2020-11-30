package com.ibm.mydev.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.mydev.config.MyDevConfiguration;
import com.ibm.mydev.dto.ReportViewPayloadDTO;
import com.ibm.mydev.dto.UserReportViewItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MyDevApiClient {

    @Autowired
    public MyDevConfiguration apiConfiguration;

    private static final String ACCEPT = "Accept";
    private static final String AUTHORIZATION_KEY = "Authorization";
    private static final String PREFER = "prefer";

    private static final String SCOPE = "all";
    private static final String GRANT_TYPE = "client_credentials";
    private static final String SCOPE_KEY = "scope";
    private static final String GRANT_TYPE_KEY = "grantType";
    private static final String CLIENT_ID_KEY = "clientId";
    private static final String CLIENT_SECRET_KEY = "clientSecret";

    private boolean initialized = false;

    private String accessToken;

    public <T> ReportViewPayloadDTO getODataPayload(String relativeAddress, Integer maxPageSize, T clazz) throws Exception {
        String stringContent = query(apiConfiguration.baseUrl, maxPageSize);
        Gson gson = new Gson();
        ReportViewPayloadDTO<T> payload = gson.fromJson(stringContent, ReportViewPayloadDTO.class);
        return payload;
    }

    public String query(String relativeAddress, Integer maxPageSize) throws Exception {

        authenticate();

        URL url = new URL(new URL(apiConfiguration.baseUrl), relativeAddress);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

        httpConnection.setReadTimeout(0);

        httpConnection.setUseCaches(false);
        httpConnection.setRequestMethod("GET");
        httpConnection.setRequestProperty(HttpHeaders.ACCEPT, "application/json");
        httpConnection.setDoOutput(false);
        httpConnection.setDoInput(true);

        List<Header> headers = new ArrayList<>();
        headers.add(new Header(AUTHORIZATION_KEY, getAuthorizationHeaderValue()));

        if (maxPageSize > 0) {
            String value = String.format("odata.maxpagesize=%s", maxPageSize);
            headers.add(new Header(PREFER, value));
        }

        addHeadersToRequest(headers, httpConnection);

        if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return readConnectionInput(httpConnection);
        }

        throw new Exception("Error !");
    }

    public void authenticate() throws Exception {

        if (this.initialized) {
            return;
        }

        URL baseURL = new URL(apiConfiguration.baseUrl);
        URL url = new URL(baseURL, apiConfiguration.authentication);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();

        httpConnection.setUseCaches(false);
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty(HttpHeaders.ACCEPT, "application/json");
        httpConnection.setDoOutput(true);
        httpConnection.setDoInput(true);

        List<Header> headers = new ArrayList<Header>();
        headers.add(new Header("cache-control", "no-cache"));

        List<RequestParameter> requestParameters = new ArrayList<RequestParameter>();
        requestParameters.add(new RequestParameter(CLIENT_ID_KEY, apiConfiguration.clientId));
        requestParameters.add(new RequestParameter(CLIENT_SECRET_KEY, apiConfiguration.password));
        requestParameters.add(new RequestParameter(GRANT_TYPE_KEY, GRANT_TYPE));
        requestParameters.add(new RequestParameter(SCOPE_KEY, SCOPE));



        addHeadersToRequest(headers, httpConnection);

        String requestBody=buildRequestBody(requestParameters);

        OutputStream outputStream = httpConnection.getOutputStream();
        outputStream.write(requestBody.getBytes());
        outputStream.flush();
        outputStream.close();

        if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            String content = readConnectionInput(httpConnection);
            JsonObject contentObject = new JsonParser().parse(content).getAsJsonObject();
            this.accessToken=contentObject.get("access_token").getAsString();
            this.initialized = true;
            System.out.println(accessToken);
            return;
        }



        throw new Exception("Error !");
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


    private  String getAuthorizationHeaderValue() {

        return "Bearer " + this.accessToken;
    }



    private static void addHeadersToRequest(List<Header> headers, HttpURLConnection httpConnection) {
        for (Header header : headers) {
            httpConnection.setRequestProperty(header.name, header.value);
        }
    }

    private static String buildRequestBody(List<RequestParameter> requestParameters) {
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

