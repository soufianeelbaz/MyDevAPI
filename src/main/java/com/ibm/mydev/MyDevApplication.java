package com.ibm.mydev;

import com.ibm.mydev.api.MyDevApiClient;
import com.ibm.mydev.dto.TranscriptReportViewPayloadDTO;
import com.ibm.mydev.dto.UserReportViewItemDTO;
import com.ibm.mydev.dto.UserReportViewPayloadDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
