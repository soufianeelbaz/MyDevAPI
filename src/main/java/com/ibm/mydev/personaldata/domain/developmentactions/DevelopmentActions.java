package com.ibm.mydev.personaldata.domain.developmentactions.repository;

import com.ibm.mydev.personaldata.domain.developmentactions.DevelopmentAction;

import java.util.List;

public interface DevelopmentActions {

    List<DevelopmentAction> getDevelopmentActions(String uid);

}
