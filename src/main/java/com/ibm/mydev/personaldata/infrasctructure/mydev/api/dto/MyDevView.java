package com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class MyDevView {

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
