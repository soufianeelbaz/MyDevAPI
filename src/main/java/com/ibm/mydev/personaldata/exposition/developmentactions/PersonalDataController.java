package com.ibm.mydev.personaldata.exposition.developmentactions;

import com.ibm.mydev.personaldata.domain.developmentactions.DevelopmentAction;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.IMyDevApiClient;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTrainingLocalView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTrainingView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTranscriptView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevUserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@SpringBootApplication
//@RequestMapping("personaldata")
public class PersonalDataController {

    @Autowired
    public IMyDevApiClient myDevApiClient;

    public static void main(String[] args) {
        SpringApplication.run(com.ibm.mydev.SpringApplication.class, args);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user/{uid}/{year}/development/actions/mydev")
    @ResponseBody
    public List<DevelopmentAction> getDevelopmentActions(@PathVariable("uid") String uid, @PathVariable("year") String year) throws Exception {

        MyDevUserView userData = myDevApiClient.getUserData(uid);
        MyDevTranscriptView transcriptData = myDevApiClient.getTranscriptData(userData.getValue().get(0).getId(), Integer.parseInt(year));
        List<String> objectIds = transcriptData.getValue()
                .stream()
                .map(transcriptItem -> transcriptItem.getTrainingId())
                .collect(Collectors.toList());
        MyDevTrainingView trainingData = myDevApiClient.getTrainingData(objectIds);
        MyDevTrainingLocalView trainingLocalData = myDevApiClient.getTrainingLocalData(userData.getValue().get(0).getLanguageId(), objectIds);

        return myDevApiClient.buildDevelopmentActions(userData, transcriptData, trainingData, trainingLocalData);
    }
}
