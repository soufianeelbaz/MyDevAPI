package com.ibm.mydev.personaldata.domain.developmentactions;

import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.TrainingItem;

import java.util.List;

public interface DevelopmentActions {

    List<TrainingItem> getDevelopmentActions(String uid, Integer year);

}
