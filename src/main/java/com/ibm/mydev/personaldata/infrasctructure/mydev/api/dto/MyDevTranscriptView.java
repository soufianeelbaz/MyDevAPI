package com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyDevTranscriptView extends MyDevView {

    @SerializedName("value")
    private List<TranscriptItem> value;

    public List<TranscriptItem> getValue() {
        return value;
    }

    public void setValue(List<TranscriptItem> value) {
        this.value = value;
    }

}
