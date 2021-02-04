package com.fast.dev.timercenter.service.service;

import com.fast.dev.timercenter.service.model.ResponseStatusModel;
import com.fast.dev.timercenter.service.model.ResponseTaskModel;
import com.fast.dev.timercenter.service.type.TaskState;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


@FeignClient(name = "timerserver/user/api")
public interface RemoteUserTimerService {


    /**
     * 查询任务
     *
     * @return
     */
    @RequestMapping("query")
    ResponseTaskModel query(@RequestParam("id") String id);


    /**
     * 心跳任务
     *
     * @param id
     * @return
     */
    @RequestMapping("heartbeat")
    Map<String, Boolean> heartbeat(@RequestParam("id") String... id);


    /**
     * 删除任务
     *
     * @param id
     * @return
     */
    @RequestMapping("remove")
    Map<String, Boolean> remove(@RequestParam("id") String... id);


    /**
     * 执行任务
     *
     * @param id
     * @return
     */
    @RequestMapping("doit")
    Object doit(@RequestParam("id") String id);


}
