package com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserItem {

    private Integer id;

    private String collaborator;

    private Integer languageId;

    public UserItem() {
    }

    public UserItem(Integer id, String collaborator, Integer languageId) {
        this.id = id;
        this.collaborator = collaborator;
        this.languageId = languageId;
    }

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

    public enum UserReportAttributes {

        USER_REFERENCE("user_ref"),
        USER_ID("user_id"),
        USER_LANGUAGE_ID("user_language_id");

        private String attribute;

        UserReportAttributes(String attribute) {
            this.attribute = attribute;
        }

        public String getAttribute() {
            return attribute;
        }
    }
}

