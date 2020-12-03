package com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TranscriptItem {

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

    private Long userId;

    @JsonProperty("userId")
    public Long getUserId() {
        return userId;
    }

    @JsonProperty("transc_user_id")
    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public enum TranscriptReportAttributes {

        TRANSC_OBJECT_ID("transc_object_id"),
        USER_LO_STATUS_GROUP_ID("user_lo_status_group_id"),
        IS_LATEST_REG_NUM("is_latest_reg_num"),
        IS_ARCHIVE("is_archive"),
        IS_REMOVED("is_removed"),
        IS_STANDALONE("is_standalone"),
        USER_LO_REG_DT("user_lo_reg_dt"),
        USER_LO_MIN_DUE_DATE("user_lo_min_due_date"),
        USER_LO_COMP_DT("user_lo_comp_dt"),
        TRAINING_PURPOSE("training_purpose"),
        TRANSC_USER_ID("transc_user_id");

        private String attribute;

        TranscriptReportAttributes(String attribute) {
            this.attribute = attribute;
        }

        public String getAttribute() {
            return attribute;
        }
    }
}
