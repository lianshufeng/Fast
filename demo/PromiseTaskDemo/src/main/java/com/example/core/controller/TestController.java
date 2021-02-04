package com.example.core.controller;

import com.fast.dev.core.util.net.apache.HttpModel;
import com.fast.dev.promise.model.RequestParmModel;
import com.fast.dev.promise.service.RemoteTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private RemoteTaskService remoteTaskService;


    @RequestMapping("put")
    public Object put() {
        RequestParmModel requestParmModel = new RequestParmModel();

        HttpModel httpModel = new HttpModel();
        httpModel.setUrl("http://www.baidu.com");
        requestParmModel.setHttp(httpModel);

        requestParmModel.setExecuteTime(6000l);

        return remoteTaskService.put(requestParmModel);
    }


    @RequestMapping("query")
    public Object query(String id) {
        return remoteTaskService.query(id);
    }


    @RequestMapping("heartbeat")
    public Object heartbeat(String id) {
        return remoteTaskService.heartbeat(id);
    }


    @RequestMapping("remove")
    public Object remove(String id) {
        return remoteTaskService.remove(id);
    }


    @RequestMapping("doit")
    public Object doit(String id) {
        return remoteTaskService.doit(id);
    }


}
