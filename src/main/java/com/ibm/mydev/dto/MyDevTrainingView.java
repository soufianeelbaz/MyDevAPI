package com.ibm.mydev.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MyDevTrainingView extends MyDevView {

    @SerializedName("value")
    private List<TrainingItem> value;

    public MyDevTrainingView() {

    }

    public List<TrainingItem> getValue() {
        if (value == null) {
            value = new ArrayList<>();
        }
        return value;
    }

    public void setValue(List<TrainingItem> value) {
        this.value = value;
    }

}
