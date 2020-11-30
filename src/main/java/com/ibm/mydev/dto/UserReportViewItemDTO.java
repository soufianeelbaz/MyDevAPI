package com.ibm.mydev.dto;

import com.google.gson.annotations.SerializedName;

public class UserReportViewItemDTO {

    @SerializedName("user_id")
    private Integer id;

    @SerializedName("user_ref")
    private String collaborator;

    @SerializedName("user_language_id")
    private Integer languageId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(String collaborator) {
        this.collaborator = collaborator;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }
}

