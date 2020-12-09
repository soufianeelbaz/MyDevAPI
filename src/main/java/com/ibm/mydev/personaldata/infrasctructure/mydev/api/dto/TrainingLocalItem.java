package com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainingLocalItem {

    private String objectId;

    private String title;

    private Integer cultureId;

    public TrainingLocalItem() {
    }

    public TrainingLocalItem(String objectId, String title, Integer cultureId) {
        this.objectId = objectId;
        this.title = title;
        this.cultureId = cultureId;
    }

    @JsonProperty("objectId")
    public String getObjectId() {
        return objectId;
    }

    @JsonProperty("training_title_local_object_id")
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("training_title_local_lo_title_local")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("cultureId")
    public Integer getCultureId() {
        return cultureId;
    }

    @JsonProperty("training_title_local_culture_id")
    public void setCultureId(Integer cultureId) {
        this.cultureId = cultureId;
    }

    public enum TrainingLocalReportAttributes {

        TRAINING_TITLE_LOCAL_OBJECT_ID("training_title_local_object_id"),
        TRAINING_TITLE_LOCAL_LO_TITLE_LOCAL("training_title_local_lo_title_local"),
        TRAINING_TITLE_LOCAL_CULTURE_ID("training_title_local_culture_id");

        private String attribute;

        TrainingLocalReportAttributes(String attribute) {
            this.attribute = attribute;
        }

        public String getAttribute() {
            return attribute;
        }
    }
}