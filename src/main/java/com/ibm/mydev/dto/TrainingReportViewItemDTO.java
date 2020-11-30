package com.ibm.mydev.dto;

import com.google.gson.annotations.SerializedName;

public class TrainingReportViewItemDTO {

    @SerializedName("lo_title")
    private String title;

    @SerializedName("lo_start_dt")
    private String startDate;

    @SerializedName("lo_end_dt")
    private String endDate;

    @SerializedName("lo_object_type")
    private String category;

    @SerializedName("lo_hours")
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
