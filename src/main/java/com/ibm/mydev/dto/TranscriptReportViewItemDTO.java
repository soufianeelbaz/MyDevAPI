package com.ibm.mydev.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class TranscriptReportViewItemDTO {

    @SerializedName("transc_object_id")
    private String trainingId;

    @SerializedName("user_lo_status_group_id")
    private Integer status;

    @SerializedName("is_latest_reg_num")
    private Integer isLatest;

    @SerializedName("is_archive")
    private Integer isArchived;

    @SerializedName("is_removed")
    private Boolean isRemoved;

    @SerializedName("is_standalone")
    private Boolean isStandalone;

    @SerializedName("user_lo_reg_dt")
    private String registrationDate;

    @SerializedName("user_lo_min_due_date")
    private String dueDate;

    @SerializedName("user_lo_comp_dt")
    private String completionDate;

    @SerializedName("training_purpose")
    private Integer trainingPurpose;

    public String getTrainingId() {
        return trainingId;
    }

    public void setTrainingId(String trainingId) {
        this.trainingId = trainingId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsLatest() {
        return isLatest;
    }

    public void setIsLatest(Integer isLatest) {
        this.isLatest = isLatest;
    }

    public Integer getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Integer isArchived) {
        this.isArchived = isArchived;
    }

    public Boolean getRemoved() {
        return isRemoved;
    }

    public void setRemoved(Boolean removed) {
        isRemoved = removed;
    }

    public Boolean getStandalone() {
        return isStandalone;
    }

    public void setStandalone(Boolean standalone) {
        isStandalone = standalone;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public Integer getTrainingPurpose() {
        return trainingPurpose;
    }

    public void setTrainingPurpose(Integer trainingPurpose) {
        this.trainingPurpose = trainingPurpose;
    }
}
