package com.ibm.mydev.personaldata.domain.developmentactions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.Clock.systemUTC;

public class DevelopmentAction {

    private String source;
    private String subject;
    private List<DevelopmentObjective> coveredObjectives;
    private String actionName;
    private LocalDate deadline;
    private LocalDateTime timestamp = LocalDateTime.now(systemUTC());
    private int developmentYear;
    private DevelopmentActionStatus developmentActionStatus;
    private DevelopmentActionType developmentActionType;
    private boolean deleted;
    private Boolean myDevTrainingMandatory;
    private String myDevTrainingCategory;

    public DevelopmentAction() {
    }

    public DevelopmentAction(String source, String subject, List<DevelopmentObjective> coveredObjectives, String actionName, LocalDate deadline, int developmentYear, DevelopmentActionStatus developmentActionStatus, DevelopmentActionType developmentActionType, Boolean myDevTrainingMandatory, String myDevTrainingCategory) {
        this.source = source;
        this.subject = subject;
        this.coveredObjectives = coveredObjectives;
        this.actionName = actionName;
        this.deadline = deadline;
        this.developmentYear = developmentYear;
        this.developmentActionStatus = developmentActionStatus;
        this.developmentActionType = developmentActionType;
        this.myDevTrainingMandatory = myDevTrainingMandatory;
        this.myDevTrainingCategory = myDevTrainingCategory;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<DevelopmentObjective> getCoveredObjectives() {
        return coveredObjectives;
    }

    public void setCoveredObjectives(List<DevelopmentObjective> coveredObjectives) {
        this.coveredObjectives = coveredObjectives;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getDevelopmentYear() {
        return developmentYear;
    }

    public void setDevelopmentYear(int developmentYear) {
        this.developmentYear = developmentYear;
    }

    public DevelopmentActionStatus getDevelopmentActionStatus() {
        return developmentActionStatus;
    }

    public void setDevelopmentActionStatus(DevelopmentActionStatus developmentActionStatus) {
        this.developmentActionStatus = developmentActionStatus;
    }

    public DevelopmentActionType getDevelopmentActionType() {
        return developmentActionType;
    }

    public void setDevelopmentActionType(DevelopmentActionType developmentActionType) {
        this.developmentActionType = developmentActionType;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getMyDevTrainingMandatory() {
        return myDevTrainingMandatory;
    }

    public void setMyDevTrainingMandatory(Boolean myDevTrainingMandatory) {
        this.myDevTrainingMandatory = myDevTrainingMandatory;
    }

    public String getMyDevTrainingCategory() {
        return myDevTrainingCategory;
    }

    public void setMyDevTrainingCategory(String myDevTrainingCategory) {
        this.myDevTrainingCategory = myDevTrainingCategory;
    }
}
