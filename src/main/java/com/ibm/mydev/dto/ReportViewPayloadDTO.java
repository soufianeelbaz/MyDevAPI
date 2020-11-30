package com.ibm.mydev.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReportViewPayloadDTO<T> {

    @SerializedName("@odata.context")
    private String context;
    @SerializedName("value")
    private List<T> value;
    @SerializedName("@odata.nextLink")
    private String nextLink;

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public List<T> getValue() {
        return value;
    }

    public void setValue(List<T> value) {
        this.value = value;
    }

    public String getNextLink() {
        return nextLink;
    }

    public void setNextLink(String nextLink) {
        this.nextLink = nextLink;
    }
}
