package com.fast.dev.promise.service;

import com.fast.dev.promise.model.RequestParmModel;
import com.fast.dev.promise.model.ResponseStatusModel;
import com.fast.dev.promise.model.ResponseTaskModel;

import java.util.Map;

public interface RemoteTaskService {


    /**
     * 添加任务
     *
     * @param requestParmModel
     * @return
     */
    RequestParmModel put(RequestParmModel requestParmModel);


    /**
     * 查询任务
     *
     * @return
     */
    ResponseTaskModel query(String id);


    /**
     * 心跳任务
     *
     * @param id
     * @return
     */
    Map<String, ResponseStatusModel> heartbeat(String... id);


    /**
     * 删除任务
     *
     * @param id
     * @return
     */
    Map<String, Boolean> remove(String... id);


    /**
     * 执行任务
     *
     * @param id
     * @return
     */
    Boolean doit(String id);


}
