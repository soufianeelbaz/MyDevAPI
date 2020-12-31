package com.ibm.mydev.personaldata.domain.developmentactions;

import java.util.List;
import java.util.Map;

public class MyDevDevelopmentActionRepresentation {

    private int statusCode;
    private String statusMessage;
    Map<Integer, List<DevelopmentAction>> developmentActionByYear;

    public MyDevDevelopmentActionRepresentation(int statusCode, String statusMessage, Map<Integer, List<DevelopmentAction>> developmentActionByYear) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.developmentActionByYear = developmentActionByYear;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Map<Integer, List<DevelopmentAction>> getDevelopmentActionByYear() {
        return developmentActionByYear;
    }
}
