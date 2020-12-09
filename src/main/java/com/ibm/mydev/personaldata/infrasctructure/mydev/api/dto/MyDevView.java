package com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public abstract class MyDevView<T> {

    private String context;

    @SerializedName("value")
    private List<T> value;

    @JsonProperty("context")
    public String getContext() {
        return context;
    }

    @JsonProperty("@odata.context")
    public void setContext(String context) {
        this.context = context;
    }

    public List<T> getValue() {
        if (value == null) {
            value = new ArrayList<>();
        }
        return value;
    }

    public void setValue(List<T> value) {
        this.value = value;
    }

    public static Boolean isEmpty(MyDevView view) {
        return view == null || view.getValue() == null || view.getValue().isEmpty();
    }

}
