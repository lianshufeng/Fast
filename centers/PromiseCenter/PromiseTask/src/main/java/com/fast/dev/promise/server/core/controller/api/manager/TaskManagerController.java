package com.fast.dev.promise.server.core.controller.api.manager;


import com.fast.dev.promise.model.RequestParmModel;
import com.fast.dev.promise.server.core.service.TaskService;
import com.fast.dev.promise.server.core.util.RequestParmUtil;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log
@RestController
@RequestMapping("manager/api")
public class TaskManagerController {


    @Autowired
    private TaskService taskService;


    /**
     * 添加任务
     *
     * @return
     */
    @RequestMapping("put")
    public Object put(@RequestBody @Validated RequestParmModel userModel) throws Exception {
        //添加任务
        return (this.taskService.put(RequestParmUtil.build(userModel)));
    }


}
