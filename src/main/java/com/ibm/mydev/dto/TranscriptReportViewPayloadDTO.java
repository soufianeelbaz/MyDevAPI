package com.ibm.mydev.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TranscriptReportViewPayloadDTO extends ReportViewPayloadDTO {

    @SerializedName("value")
    private List<TranscriptReportViewItemDTO> value;

    public List<TranscriptReportViewItemDTO> getValue() {
        return value;
    }

    public void setValue(List<TranscriptReportViewItemDTO> value) {
        this.value = value;
    }

}
