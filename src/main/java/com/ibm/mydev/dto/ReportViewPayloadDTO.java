package com.ibm.mydev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ReportViewPayloadDTO {

    private String context;

    @JsonProperty("context")
    public String getContext() {
        return context;
    }

    @JsonProperty("@odata.context")
    public void setContext(String context) {
        this.context = context;
    }

}
