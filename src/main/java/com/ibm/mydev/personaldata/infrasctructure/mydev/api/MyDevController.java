package com.ibm.mydev.personaldata.infrasctructure.mydev.api;

import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTrainingLocalView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTrainingView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevTranscriptView;
import com.ibm.mydev.personaldata.infrasctructure.mydev.api.dto.MyDevUserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("portal/mydev")
public class MyDevController {

    @Autowired
    public IMyDevApiClient myDevApiClient;

    List<String> objectIds = Arrays.asList("5c194dc6-3840-4d4b-843e-04668923ca7e", "3d11038a-3928-4a00-9dde-06a5de4ce262", "bf7efe88-382f-4837-9a67-13863aeab110", "b9588180-1868-43bd-99d4-14e32e46d2fb", "3e677399-2acb-444d-975f-17f37ea16fb5", "42487c81-beb1-428a-a98f-1e1b24227bae", "0ffe0234-6a77-43b2-bf2f-1f37e9dfe178", "41d1abd8-ae5a-471e-bf61-2a204d720283", "c67f52d2-ad39-4eb3-ba06-2aed1045894d", "1a440032-2593-491d-8f6e-2c45823a0377", "0cf3da89-8e37-44ed-98c3-37d6fd7834a6", "bcbdd8e1-688c-4289-ac60-485e92849d62", "0e041767-88cd-4154-b60c-492e4b434efc", "bd2377e4-a4d9-4f4d-82d9-4e7932735d2e", "626d650b-4bce-4336-a9ea-5428bb90b64e", "ce526cdc-4408-4aef-be4d-56722079f9a1", "35671ad6-e087-4fe3-ad4c-598526177fa7", "367ad50c-aa13-43b0-a710-65745a7b630f", "508e9058-d538-45bd-bb28-6b55dc088123", "7da80186-2f40-4c34-9283-7125857bcaef", "30a257ec-bb01-45d6-960b-744b9bb49460", "54438bbd-24c2-4e63-8537-8a94d5437cab", "e67aa1f6-3660-48ae-96f7-8c3cd73a3419", "dfc2d3fb-ea52-45ab-a6ec-8eeb05f17a26", "c645d165-490b-4874-8a09-8f27faac7029", "2e55cdf8-dd47-489f-a4af-9d39fecb7897", "56cd82c2-92d6-4318-97bd-9e21a8624a61", "f66faf9e-6b2e-4bc9-889c-a78aabe62866", "0c64dd29-ede9-462d-9d32-ab0bada9fa73", "db7e4868-a3c3-4ac2-ac11-bc4f9afcd1a0", "6264a50c-0dee-4491-b01b-be1a86f0c5a5", "af28d40f-b840-434b-92e8-c3e2ad831b1f", "411903eb-73e5-4e64-9126-c5798fa6d5c5", "08d0ad57-9363-4244-927f-c604326b61b3", "f8b9b6e5-9950-4aa4-8c2e-d235469b960d", "7b3f7c7a-6e87-4255-9379-d6315c20b695", "2afc25ac-48a1-4cd5-9f1d-d6d23ca1cffa", "4ee50daf-00df-47a5-afed-d9445dda391d", "25fb7279-9971-451e-9dfb-e073ea5b90f0", "4f33f2ea-a285-46ec-8176-e1509b4bf094", "ba9ca09a-ee97-44ea-8fec-f145ec6f37e8", "ddd7c734-c25c-4a54-9f06-f6711c5c22af", "a14668b7-51c0-4412-96db-fcb9dfe07f3c");

    @RequestMapping(method = RequestMethod.GET, value = "/user/{uid}")
    @ResponseBody
    public MyDevUserView users(@PathVariable("uid") String uid) throws Exception {

        return myDevApiClient.getUserData(uid);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/transcript/{userId}/{year}")
    @ResponseBody
    public MyDevTranscriptView users(@PathVariable("userId") Integer userId,
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
    public MyDevTrainingLocalView trainingsLocal(@PathVariable("cultureId") Integer cultureId) throws Exception {

        return myDevApiClient.getTrainingLocalData(cultureId, objectIds);
    }

}

