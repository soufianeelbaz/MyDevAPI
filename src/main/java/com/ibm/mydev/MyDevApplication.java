package com.ibm.mydev;

import com.ibm.mydev.api.MyDevApiClient;
import com.ibm.mydev.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@SpringBootApplication
@RestController
public class MyDevApplication {

    @Autowired
    public MyDevApiClient myDevApiClient;

    public static void main(String[] args) {
        SpringApplication.run(MyDevApplication.class, args);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user/{uid}")
    @ResponseBody
    public ResponseEntity<UserReportViewPayloadDTO> users(@PathVariable("uid") String uid) throws Exception {

        return myDevApiClient.getUserData(uid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user/{id}/{year}")
    @ResponseBody
    public ResponseEntity<TranscriptReportViewPayloadDTO> users(@PathVariable("id") Long id,
                                                                @PathVariable("year") Integer year) throws Exception {
        return myDevApiClient.getTranscriptData(id, year);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/training/{objectId}")
    @ResponseBody
    public ResponseEntity<TrainingReportViewPayloadDTO> trainings(@PathVariable("objectId") String objectId) throws Exception {

        return myDevApiClient.getTrainingData(objectId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/training/local/{cultureId}")
    @ResponseBody
    public ResponseEntity<TrainingLocalViewPayloadDTO> trainingsLocal(@PathVariable("cultureId") Long cultureId) throws Exception {

        return myDevApiClient.getTrainingLocalData(cultureId, Arrays.asList("2d3ece83-532d-4219-be54-e80a273bb67a", "150056f6-7b54-40f8-bcc9-646a06ddf0af", "1f5cb93b-cc7a-41b3-9ccf-852574fd4fe1"));
    }

}
