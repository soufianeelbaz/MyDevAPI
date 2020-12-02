package com.ibm.mydev;

import com.ibm.mydev.api.IMyDevApiClient;
import com.ibm.mydev.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@SpringBootApplication
@RestController
public class MyDevApplication {

    @Autowired
    public IMyDevApiClient myDevApiClient;

    public static void main(String[] args) {
        SpringApplication.run(MyDevApplication.class, args);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/user/{uid}")
    @ResponseBody
    public MyDevUserView users(@PathVariable("uid") String uid) throws Exception {

        return myDevApiClient.getUserData(uid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/transcript/{userId}/{year}")
    @ResponseBody
    public MyDevTranscriptView users(@PathVariable("userId") Long userId,
                                                     @PathVariable("year") Integer year) throws Exception {
        return myDevApiClient.getTranscriptData(userId, year);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/training")
    @ResponseBody
    public MyDevTrainingView trainings() throws Exception {

        return myDevApiClient.getTrainingData(Arrays.asList("2d3ece83-532d-4219-be54-e80a273bb67a", "150056f6-7b54-40f8-bcc9-646a06ddf0af", "1f5cb93b-cc7a-41b3-9ccf-852574fd4fe1"));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/training/local/{cultureId}")
    @ResponseBody
    public MyDevTrainingLocalView trainingsLocal(@PathVariable("cultureId") Long cultureId) throws Exception {

        return myDevApiClient.getTrainingLocalData(cultureId, Arrays.asList("2d3ece83-532d-4219-be54-e80a273bb67a", "150056f6-7b54-40f8-bcc9-646a06ddf0af", "1f5cb93b-cc7a-41b3-9ccf-852574fd4fe1"));
    }

}
