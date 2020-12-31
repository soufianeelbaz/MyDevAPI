package com.ibm.mydev.personaldata.domain.developmentactions;

import java.util.Set;

public interface MyDevDevelopmentActions {

    MyDevDevelopmentAction findBySubjectAndDevelopmentYear(String subject, int developmentYear, String locale);

}
