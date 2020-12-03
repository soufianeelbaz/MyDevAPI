package com.ibm.mydev.personaldata.exposition.developmentactions;

import com.ibm.mydev.personaldata.domain.developmentactions.DevelopmentAction;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("personaldata")
public class PersonalDataController {

    @RequestMapping(method = RequestMethod.GET, value = "/user/{id}/development/actions/mydev")
    @ResponseBody
    public List<DevelopmentAction> getDevelopmentActions(@PathVariable("uid") String uid) throws Exception {

        return null;
    }
}
