package com.fast.dev.timercenter.service.service;

import com.fast.dev.timercenter.service.model.RequestParmModel;
import com.fast.dev.timercenter.service.model.ResponseTaskModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "timerserver/manager/api")
public interface RemoteManagerTimerService extends RemoteUserTimerService {


    /**
     * 添加任务
     *
     * @param requestParmModel
     * @return
     */
    @RequestMapping("put")
    ResponseTaskModel put(@RequestBody RequestParmModel requestParmModel);


}
