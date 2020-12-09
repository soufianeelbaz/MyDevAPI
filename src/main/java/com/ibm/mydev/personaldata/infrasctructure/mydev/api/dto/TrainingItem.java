package com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainingItem {

    private String title;

    private String startDate;

    private String endDate;

    private String category;

    private Integer hours;

    private String objectId;

    public TrainingItem() {
    }

    public TrainingItem(String title, String startDate, String endDate, String category, Integer hours, String objectId) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
        this.hours = hours;
        this.objectId = objectId;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("lo_title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("startDate")
    public String getStartDate() {
        return startDate;
    }

    @JsonProperty("lo_start_dt")
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @JsonProperty("endDate")
    public String getEndDate() {
        return endDate;
    }

    @JsonProperty("lo_end_dt")
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @JsonProperty("category")
    public String getCategory() {
        return category;
    }

    @JsonProperty("lo_object_type")
    public void setCategory(String category) {
        this.category = category;
    }

    @JsonProperty("hours")
    public Integer getHours() {
        return hours;
    }

    @JsonProperty("lo_hours")
    public void setHours(Integer hours) {
        this.hours = hours;
    }

    @JsonProperty("objectId")
    public String getObjectId() {
        return objectId;
    }

    @JsonProperty("object_id")
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public enum TrainingReportAttributes {

        OBJECT_ID("object_id"),
        LO_TITLE("lo_title"),
        LO_START_DT("lo_start_dt"),
        LO_END_DT("lo_end_dt"),
        LO_OBJECT_TYPE("lo_object_type"),
        LO_HOURS("lo_hours");

        private String attribute;

        TrainingReportAttributes(String attribute) {
            this.attribute = attribute;
        }

        public String getAttribute() {
            return attribute;
        }
    }
}

