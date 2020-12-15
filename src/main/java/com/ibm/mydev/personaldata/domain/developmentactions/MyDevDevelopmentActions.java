package com.ibm.mydev.personaldata.domain.developmentactions;

import java.util.Set;

public interface MyDevDevelopmentActions {

    Set<DevelopmentAction> findBySubjectAndDevelopmentYear(String subject, int developmentYear, String locale);

}
