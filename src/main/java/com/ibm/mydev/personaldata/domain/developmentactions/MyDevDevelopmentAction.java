package com.ibm.mydev.personaldata.domain.developmentactions;

import java.util.Set;

public class MyDevDevelopmentAction {

    private final int statusCode;
    private final String statusMessage;
    private final Set<DevelopmentAction> developmentActions;

    public MyDevDevelopmentAction(int statusCode, String statusMessage, Set<DevelopmentAction> developmentActions) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.developmentActions = developmentActions;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Set<DevelopmentAction> getDevelopmentActions() {
        return developmentActions;
    }
}
