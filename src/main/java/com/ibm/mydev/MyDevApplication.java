package com.ibm.mydev;

import com.ibm.mydev.api.MyDevApiClient;
import com.ibm.mydev.config.MyDevConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MyDevApplication {

    @Autowired
    public MyDevApiClient myDevApiClient;

    public static void main(String[] args) {
        SpringApplication.run(MyDevApplication.class, args);
    }

    @RequestMapping(value = "/authenticate")
    public String authenticate() {
        try {
            myDevApiClient.authenticate();
            return "Authenticated!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
