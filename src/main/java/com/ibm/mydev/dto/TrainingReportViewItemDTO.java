package com.ibm.mydev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainingReportViewItemDTO {

    @JsonProperty("lo_title")
    private String title;

    @JsonProperty("lo_start_dt")
    private String startDate;

    @JsonProperty("lo_end_dt")
    private String endDate;

    @JsonProperty("lo_object_type")
    private String category;

    @JsonProperty("lo_hours")
    private Integer hours;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }
}
