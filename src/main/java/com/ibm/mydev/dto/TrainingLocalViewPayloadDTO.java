package com.ibm.mydev.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrainingLocalViewPayloadDTO extends ReportViewPayloadDTO {

    @SerializedName("value")
    private List<TrainingLocalViewItemDTO> value;

    public List<TrainingLocalViewItemDTO> getValue() {
        return value;
    }

    public void setValue(List<TrainingLocalViewItemDTO> value) {
        this.value = value;
    }

}
