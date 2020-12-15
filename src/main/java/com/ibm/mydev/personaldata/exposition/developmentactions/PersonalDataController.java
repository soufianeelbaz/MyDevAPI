package com.ibm.mydev.personaldata.exposition.developmentactions;

import com.ibm.mydev.personaldata.domain.developmentactions.DevelopmentAction;
import com.ibm.mydev.personaldata.domain.developmentactions.MyDevDevelopmentActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
public class PersonalDataController {

    @Autowired
    public MyDevDevelopmentActions mydevDevelopmentActions;

    @RequestMapping(method = RequestMethod.GET, value = "/{collaboratorId}/developmentplan/actions/mydev/{year}/{locale}")
    @ResponseBody
    public Set<DevelopmentAction> getDevelopmentActions(@PathVariable("collaboratorId") String collaboratorId, @PathVariable("year") int year, @PathVariable("locale") String locale) throws Exception {

        return mydevDevelopmentActions.findBySubjectAndDevelopmentYear(collaboratorId, year, locale);
    }
}
