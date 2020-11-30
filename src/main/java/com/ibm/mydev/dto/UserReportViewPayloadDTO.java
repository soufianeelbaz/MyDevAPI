package com.ibm.mydev.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserReportViewPayloadDTO extends ReportViewPayloadDTO {

    @SerializedName("value")
    private List<UserReportViewItemDTO> value;

    public List<UserReportViewItemDTO> getValue() {
        return value;
    }

    public void setValue(List<UserReportViewItemDTO> value) {
        this.value = value;
    }
}
