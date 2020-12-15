package com.ibm.mydev.personaldata.domain.developmentactions;

public class DevelopmentActionType {

    private String label;
    private String code;

    public DevelopmentActionType(String label, String code) {
        this.label = label;
        this.label = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
