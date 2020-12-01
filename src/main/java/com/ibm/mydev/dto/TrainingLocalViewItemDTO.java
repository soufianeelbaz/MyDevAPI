package com.ibm.mydev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainingLocalViewItemDTO {

    private String objectId;

    private String title;

    private Long cultureId;


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
    public Long getCultureId() {
        return cultureId;
    }

    @JsonProperty("training_title_local_culture_id")
    public void setCultureId(Long cultureId) {
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