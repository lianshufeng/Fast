package com.fast.dev.promise.server.core.controller.api;

import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.promise.model.RequestParmModel;
import com.fast.dev.promise.model.ResponseTaskModel;
import com.fast.dev.promise.model.TaskModel;
import com.fast.dev.promise.server.core.service.TaskService;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log
@RestController
@RequestMapping("api")
public class TaskController  {

    @Autowired
    private TaskService taskService;


    /**
     * 查询任务
     *
     * @param id
     * @return
     */
    @RequestMapping("query")
    public Object query(String id) throws Exception {
        TaskModel taskModel = this.taskService.query(id);
        if (taskModel == null) {
            return null;
        }
        //拷贝数据
        ResponseTaskModel responseTaskModel = new ResponseTaskModel();
        BeanUtils.copyProperties(taskModel, responseTaskModel);
        responseTaskModel.setStatus(this.taskService.getResponseModel(taskModel.getId()));
        return (responseTaskModel);
    }

    /**
     * 心跳
     *
     * @param id
     * @return
     */
    @RequestMapping("heartbeat")
    public Object heartbeat(String... id) {
        return (this.taskService.heartbeat(id));
    }


    /**
     * 删除任务
     *
     * @param id
     * @return
     */
    @RequestMapping("remove")
    public Object remove(String... id) {
        return this.taskService.removeTaskTableByTaskId(id);
    }


    /**
     * 执行任务
     *
     * @param id
     * @return
     */
    @RequestMapping("doit")
    public Object doit(String id) {
        return this.taskService.doit(id);
    }

}
