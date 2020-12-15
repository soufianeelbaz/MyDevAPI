package com.ibm.mydev.personaldata.infrasctructure.mydev;

import com.ibm.mydev.personaldata.domain.developmentactions.DevelopmentActionType;
import com.ibm.mydev.personaldata.domain.developmentactions.DevelopmentActionTypes;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DevelopmentActionTypesImpl implements DevelopmentActionTypes {

    @Override
    public List<DevelopmentActionType> findAll(String locale) {

        return  Arrays.asList(
                new DevelopmentActionType("Formations catalogues", "INTERNAL_TRAININGS"),
                new DevelopmentActionType("Formations HORS catalogues", "EXTERNAL_TRAININGS"),
                new DevelopmentActionType("Tutoring / Mentoring / Coaching", "COACHING"),
                new DevelopmentActionType("Séminaires et événements professionnels", "PROFESSIONAL_EVENTS"),
                new DevelopmentActionType("Développement « On the Job »", "OJD_LEARNING"),
                new DevelopmentActionType("Partenariats", "EXTERNAL_INCUBATION_PARTNERSHIPS"),
                new DevelopmentActionType("Autre", "OTHER")
        );
    }
}