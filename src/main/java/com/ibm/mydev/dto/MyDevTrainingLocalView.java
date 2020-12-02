package com.ibm.mydev.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyDevTrainingLocalView extends MyDevView {

    @SerializedName("value")
    private List<TrainingLocalItem> value;

    public List<TrainingLocalItem> getValue() {
        return value;
    }

    public void setValue(List<TrainingLocalItem> value) {
        this.value = value;
    }

}
