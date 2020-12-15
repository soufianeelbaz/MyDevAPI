package com.ibm.mydev.personaldata.domain.developmentactions;

import java.util.List;

public interface DevelopmentActionTypes {

    List<DevelopmentActionType> findAll(String locale);
}
