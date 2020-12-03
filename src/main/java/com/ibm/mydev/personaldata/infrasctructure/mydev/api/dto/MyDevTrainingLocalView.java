package com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MyDevTrainingLocalView extends MyDevView {

    @SerializedName("value")
    private List<TrainingLocalItem> value;

    public MyDevTrainingLocalView() {
    }



    public List<TrainingLocalItem> getValue() {
        if (value == null) {
            value = new ArrayList<>();
        }
        return value;
    }

    public void setValue(List<TrainingLocalItem> value) {
        this.value = value;
    }

}
