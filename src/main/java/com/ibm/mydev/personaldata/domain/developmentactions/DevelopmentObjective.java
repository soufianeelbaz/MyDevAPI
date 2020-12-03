package com.ibm.mydev.personaldata.domain.developmentactions;

import java.time.LocalDateTime;

import static java.time.Clock.systemUTC;
import static java.time.LocalDateTime.now;


public class DevelopmentObjective {

    private String developmentYear;
    private String source;
    private String subject;
    private String skill;
    private String skillLevel;
    private LocalDateTime creationDate = now(systemUTC());

    public boolean isCollaboratorObjective() {
        return this.source.equals(this.subject);
    }

    public boolean isManagerObjective() {
        return !isCollaboratorObjective();
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public String getDevelopmentYear() {
        return developmentYear;
    }

    public void setDevelopmentYear(String developmentYear) {
        this.developmentYear = developmentYear;
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

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}