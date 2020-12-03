package com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyDevUserView extends MyDevView {

    @SerializedName("value")
    private List<UserItem> value;

    public List<UserItem> getValue() {
        return value;
    }

    public void setValue(List<UserItem> value) {
        this.value = value;
    }
}
