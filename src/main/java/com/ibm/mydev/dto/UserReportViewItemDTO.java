package com.ibm.mydev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserReportViewItemDTO {

    private Integer id;

    private String collaborator;

    private Integer languageId;

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("user_id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("collaborator")
    public String getCollaborator() {
        return collaborator;
    }

    @JsonProperty("user_ref")
    public void setCollaborator(String collaborator) {
        this.collaborator = collaborator;
    }

    @JsonProperty("languageId")
    public Integer getLanguageId() {
        return languageId;
    }

    @JsonProperty("user_language_id")
    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

}

