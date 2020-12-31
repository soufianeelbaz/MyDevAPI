package com.ibm.mydev.personaldata.exposition.developmentactions;

import com.google.common.collect.Lists;
import com.ibm.mydev.personaldata.domain.developmentactions.DevelopmentAction;
import com.ibm.mydev.personaldata.domain.developmentactions.MyDevDevelopmentAction;
import com.ibm.mydev.personaldata.domain.developmentactions.MyDevDevelopmentActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class PersonalDataController {

    @Autowired
    public MyDevDevelopmentActions mydevDevelopmentActions;

    @RequestMapping(method = RequestMethod.GET, value = "/{collaboratorId}/developmentplan/actions/mydev/{year}/{locale}")
    @ResponseBody
    public Map<Integer, List<DevelopmentAction>> getDevelopmentActions(@PathVariable("collaboratorId") String collaboratorId, @PathVariable("year") int year, @PathVariable("locale") String locale) throws Exception {

        MyDevDevelopmentAction developmentActions = mydevDevelopmentActions.findBySubjectAndDevelopmentYear(collaboratorId, year, locale);

        Comparator<DevelopmentAction> actionComparatorByDeadline = Comparator.comparing(developmentAction -> developmentAction.getDeadline());
        Comparator<DevelopmentAction> actionComparatorByActionType = Comparator.comparing(developmentAction -> developmentAction.getDevelopmentActionType().getLabel().trim());

        List<DevelopmentAction> sortDevelopmentAction = developmentActions.getDevelopmentActions().stream()
                .sorted(actionComparatorByDeadline.reversed().thenComparing(actionComparatorByActionType))
                .collect(Collectors.toList());

        Map<Integer, List<DevelopmentAction>> groupingDevelopmentAction = sortDevelopmentAction.stream()
                .collect(Collectors.groupingBy(developmentAction -> developmentAction.getDevelopmentYear()));

        return groupingDevelopmentAction;
    }
}
