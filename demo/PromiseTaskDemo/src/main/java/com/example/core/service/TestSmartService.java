package com.example.core.service;

import com.fast.dev.promise.model.RequestParmModel;
import com.fast.dev.promise.smart.annotations.PromiseTask;
import org.springframework.stereotype.Service;

@Service
public class TestSmartService {


    @PromiseTask
    public Long test(String user, RequestParmModel requestParmModel) throws Exception {
        System.out.println(user);
        //模拟执行很长时间
        Thread.sleep(30000);
        return System.currentTimeMillis();
    }

}
