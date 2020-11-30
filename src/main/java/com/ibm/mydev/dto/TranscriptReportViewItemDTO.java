package com.ibm.mydev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TranscriptReportViewItemDTO {

    private String trainingId;

    private Integer status;

    private Integer isLatest;

    private Integer isArchived;

    private Boolean isRemoved;

    private Boolean isStandalone;

    private String registrationDate;

    private String dueDate;

    private String completionDate;

    private Integer trainingPurpose;

    @JsonProperty("trainingId")
    public String getTrainingId() {
        return trainingId;
    }

    @JsonProperty("transc_object_id")
    public void setTrainingId(String trainingId) {
        this.trainingId = trainingId;
    }

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("user_lo_status_group_id")
    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonProperty("isLatest")
    public Integer getIsLatest() {
        return isLatest;
    }

    @JsonProperty("is_latest_reg_num")
    public void setIsLatest(Integer isLatest) {
        this.isLatest = isLatest;
    }

    @JsonProperty("isArchived")
    public Integer getIsArchived() {
        return isArchived;
    }

    @JsonProperty("is_archive")
    public void setIsArchived(Integer isArchived) {
        this.isArchived = isArchived;
    }

    @JsonProperty("isRemoved")
    public Boolean getRemoved() {
        return isRemoved;
    }

    @JsonProperty("is_removed")
    public void setRemoved(Boolean removed) {
        isRemoved = removed;
    }

    @JsonProperty("isStandalone")
    public Boolean getStandalone() {
        return isStandalone;
    }

    @JsonProperty("is_standalone")
    public void setStandalone(Boolean standalone) {
        isStandalone = standalone;
    }

    @JsonProperty("registrationDate")
    public String getRegistrationDate() {
        return registrationDate;
    }

    @JsonProperty("user_lo_reg_dt")
    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    @JsonProperty("dueDate")
    public String getDueDate() {
        return dueDate;
    }

    @JsonProperty("user_lo_min_due_date")
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    @JsonProperty("completionDate")
    public String getCompletionDate() {
        return completionDate;
    }

    @JsonProperty("user_lo_comp_dt")
    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    @JsonProperty("trainingPurpose")
    public Integer getTrainingPurpose() {
        return trainingPurpose;
    }

    @JsonProperty("training_purpose")
    public void setTrainingPurpose(Integer trainingPurpose) {
        this.trainingPurpose = trainingPurpose;
    }
}
