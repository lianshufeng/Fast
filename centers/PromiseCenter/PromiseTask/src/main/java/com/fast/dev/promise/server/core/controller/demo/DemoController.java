package com.fast.dev.promise.server.core.controller.demo;

import com.fast.dev.core.util.net.apache.HttpModel;
import com.fast.dev.promise.model.RequestParmModel;
import com.fast.dev.promise.server.core.dao.TaskTableDao;
import com.fast.dev.promise.server.core.factory.DefaultRequestParm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api")
public class DemoController {

    @Autowired
    private TaskTableDao taskDao;


    @RequestMapping("demo")
    public RequestParmModel demo() {
        RequestParmModel requestParmModel = DefaultRequestParm.get();
        requestParmModel.setHttp(HttpModel.builder().url("http://www.baidu.com").build());
        return requestParmModel;
    }


}
