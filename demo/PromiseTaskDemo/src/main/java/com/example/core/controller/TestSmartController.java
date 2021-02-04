package com.example.core.controller;

import com.example.core.service.TestSmartService;
import com.fast.dev.promise.model.RequestParmModel;
import com.fast.dev.promise.util.PromiseTaskFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class TestSmartController {

    @Autowired
    private TestSmartService testService;


    @RequestMapping("smart")
    public Object smart(String user) throws Exception {
        RequestParmModel requestParmModel = PromiseTaskFactory.builder().url("https://www.baidu.com").executeTime(10000l).build().build();
        return this.testService.test(user, requestParmModel);
    }

}
