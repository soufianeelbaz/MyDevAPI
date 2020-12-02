package com.ibm.mydev.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyDevTrainingView extends MyDevView {

    @SerializedName("value")
    private List<TrainingItem> value;

    public List<TrainingItem> getValue() {
        return value;
    }

    public void setValue(List<TrainingItem> value) {
        this.value = value;
    }

}
