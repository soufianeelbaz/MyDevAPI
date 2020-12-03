package com.ibm.mydev;

import com.ibm.mydev.api.IMyDevApiClient;
import com.ibm.mydev.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@RestController
public class MyDevApplication {

    @Autowired
    public IMyDevApiClient myDevApiClient;

    List<String> objectIds = Arrays.asList("f79f9835-f1b3-4dbf-9049-0252bad79259", "5c194dc6-3840-4d4b-843e-04668923ca7e", "3d11038a-3928-4a00-9dde-06a5de4ce262", "db4f9152-6fb0-4d44-bf5a-0ae31521053f", "bf7efe88-382f-4837-9a67-13863aeab110", "b9588180-1868-43bd-99d4-14e32e46d2fb", "3e677399-2acb-444d-975f-17f37ea16fb5", "e4787a7a-069d-45f3-96df-186ead06ec7b", "42487c81-beb1-428a-a98f-1e1b24227bae", "0ffe0234-6a77-43b2-bf2f-1f37e9dfe178", "41d1abd8-ae5a-471e-bf61-2a204d720283", "c67f52d2-ad39-4eb3-ba06-2aed1045894d", "1a440032-2593-491d-8f6e-2c45823a0377", "2f1ae7ba-2213-4fff-8ce2-308929e97514", "deee384e-b179-40d0-a0d4-3355e06d8f00", "0cf3da89-8e37-44ed-98c3-37d6fd7834a6", "2d392323-5f6e-4341-950b-4773c77d2292", "bcbdd8e1-688c-4289-ac60-485e92849d62", "0e041767-88cd-4154-b60c-492e4b434efc", "5d463ffb-8bd1-4f79-82bc-4998588d452e", "bd2377e4-a4d9-4f4d-82d9-4e7932735d2e", "626d650b-4bce-4336-a9ea-5428bb90b64e", "ce526cdc-4408-4aef-be4d-56722079f9a1", "35671ad6-e087-4fe3-ad4c-598526177fa7", "367ad50c-aa13-43b0-a710-65745a7b630f", "847463dc-ea25-43e1-b648-665dbadd662c", "508e9058-d538-45bd-bb28-6b55dc088123", "9c691d2c-085d-4c41-b913-6bee2a2c811f", "642faa67-ddb3-44b8-ab48-6c887d308b2a", "7da80186-2f40-4c34-9283-7125857bcaef", "30a257ec-bb01-45d6-960b-744b9bb49460", "5c4ab6cd-cd0c-4f9c-b2c2-7f2f852eeebe", "72d201c3-7dfd-4d11-b936-89072f6080ed", "8f79eae3-2af3-40f4-b9e2-89b9c9fb9f8f", "5d87f598-9bd6-48df-88cd-8a0e4198243c", "54438bbd-24c2-4e63-8537-8a94d5437cab", "e67aa1f6-3660-48ae-96f7-8c3cd73a3419", "dfc2d3fb-ea52-45ab-a6ec-8eeb05f17a26", "c645d165-490b-4874-8a09-8f27faac7029", "2e55cdf8-dd47-489f-a4af-9d39fecb7897", "56cd82c2-92d6-4318-97bd-9e21a8624a61", "1d2d400c-961b-43ef-b525-a07374a8afd3", "9f5dd259-b3b5-4acb-b55e-a558c8656b57", "f66faf9e-6b2e-4bc9-889c-a78aabe62866", "c2a944ce-6396-4a7d-9f35-aa7bbeee21a2", "b1829525-0f8c-430b-9584-aaf70c77e074", "0c64dd29-ede9-462d-9d32-ab0bada9fa73", "5ebd6840-c226-4446-80d3-b0f8f354ce38", "c9d53ac1-75b1-45ae-800d-b49adf681f80", "db7e4868-a3c3-4ac2-ac11-bc4f9afcd1a0", "c5e0c106-84f1-4e6c-8008-bcecb78c898c", "6264a50c-0dee-4491-b01b-be1a86f0c5a5", "af28d40f-b840-434b-92e8-c3e2ad831b1f", "411903eb-73e5-4e64-9126-c5798fa6d5c5", "08d0ad57-9363-4244-927f-c604326b61b3", "351225c9-9c67-4a5a-ac85-c615766919c1", "dc783563-2afa-46b3-ac3a-cf40ca6c29a5", "f8b9b6e5-9950-4aa4-8c2e-d235469b960d", "5214b099-3aad-44ac-b5cb-d2602a1ecc99", "7b3f7c7a-6e87-4255-9379-d6315c20b695", "2afc25ac-48a1-4cd5-9f1d-d6d23ca1cffa", "4ee50daf-00df-47a5-afed-d9445dda391d", "25fb7279-9971-451e-9dfb-e073ea5b90f0", "4f33f2ea-a285-46ec-8176-e1509b4bf094", "6ee58902-70ec-4287-b68d-e4298cbeebe4", "ba9ca09a-ee97-44ea-8fec-f145ec6f37e8", "ddd7c734-c25c-4a54-9f06-f6711c5c22af", "dadd4efd-a713-4475-bff5-fa27399e405d", "a14668b7-51c0-4412-96db-fcb9dfe07f3c");

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
        return myDevApiClient.getTrainingData(objectIds);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/training/local/{cultureId}")
    @ResponseBody
    public MyDevTrainingLocalView trainingsLocal(@PathVariable("cultureId") Long cultureId) throws Exception {

        return myDevApiClient.getTrainingLocalData(cultureId, objectIds);
    }

}
