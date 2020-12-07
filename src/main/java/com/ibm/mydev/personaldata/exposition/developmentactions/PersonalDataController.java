package com.ibm.mydev.personaldata.exposition.developmentactions;

import com.ibm.mydev.personaldata.domain.developmentactions.DevelopmentAction;
import com.ibm.mydev.personaldata.domain.developmentactions.DevelopmentActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import rx.schedulers.Schedulers;

import java.util.List;

@RestController
public class PersonalDataController {

    @Autowired
    public DevelopmentActions developmentActions;

    @RequestMapping(method = RequestMethod.GET, value = "/user/{uid}/{year}/development/actions/mydev")
    @ResponseBody
    public DeferredResult<List<DevelopmentAction>> getDevelopmentActions(@PathVariable("uid") String uid, @PathVariable("year") Integer year) throws Exception {

        final DeferredResult<List<DevelopmentAction>> deferredResult = new DeferredResult<>();
        developmentActions.getDeveloppementActionsObservable(uid, year)
                .subscribeOn(Schedulers.io())
                .subscribe(developmentActions -> deferredResult.setResult(developmentActions));
        return deferredResult;
    }
}
