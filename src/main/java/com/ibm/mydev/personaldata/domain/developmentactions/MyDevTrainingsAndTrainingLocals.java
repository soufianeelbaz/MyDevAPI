package com.ibm.mydev.personaldata.domain.developmentactions;

import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTrainingLocalView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTrainingView;

public class MyDevTrainingsAndTrainingLocals {

    private MyDevTrainingLocalView trainingLocal;

    private MyDevTrainingView training;

    public MyDevTrainingLocalView getTrainingLocal() {
        return trainingLocal;
    }

    public void setTrainingLocal(MyDevTrainingLocalView trainingLocal) {
        this.trainingLocal = trainingLocal;
    }

    public MyDevTrainingView getTraining() {
        return training;
    }

    public void setTraining(MyDevTrainingView training) {
        this.training = training;
    }
}
