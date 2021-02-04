package com.fast.dev.timercenter.server.core.controller.demo;

import com.fast.dev.timercenter.server.core.factory.DefaultRequestParm;
import com.fast.dev.timercenter.service.model.RequestParmModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api")
public class DemoController {


    @Autowired
    private DefaultRequestParm defaultRequestParm;


    @RequestMapping("demo")
    public RequestParmModel demo() {
        return defaultRequestParm.get();
    }


}
