package com.ibm.mydev.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrainingReportViewPayloadDTO extends ReportViewPayloadDTO {

    @SerializedName("value")
    private List<TrainingReportViewItemDTO> value;

    public List<TrainingReportViewItemDTO> getValue() {
        return value;
    }

    public void setValue(List<TrainingReportViewItemDTO> value) {
        this.value = value;
    }

}
