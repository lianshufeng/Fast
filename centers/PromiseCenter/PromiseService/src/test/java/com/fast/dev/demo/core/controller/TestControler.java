package com.fast.dev.demo.core.controller;

import com.fast.dev.core.util.net.apache.HttpModel;
import com.fast.dev.promise.model.RequestParmModel;
import com.fast.dev.promise.service.RemoteTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestControler {

    @Autowired
    private RemoteTaskService remoteTaskService;


    @RequestMapping("put")
    public Object put(){
        RequestParmModel requestParmModel = new RequestParmModel();

        HttpModel httpModel = new HttpModel();
        httpModel.setUrl("http://www.baidu.com");
        requestParmModel.setHttp(httpModel);

        return remoteTaskService.put(requestParmModel);
    }


}
