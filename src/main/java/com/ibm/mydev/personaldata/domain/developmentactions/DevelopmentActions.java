package com.ibm.mydev.personaldata.domain.developmentactions;

import rx.Observable;

import java.util.List;

public interface DevelopmentActions {

    List<DevelopmentAction> getDevelopmentActions(String uid, String year) throws Exception;

    Observable<List<DevelopmentAction>> getDeveloppementActionsObservable(String uid, Integer year);
}
