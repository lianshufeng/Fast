package com.fast.dev.timercenter.server.core.service;

import com.fast.dev.timercenter.server.core.conf.TimerCenterServerConf;
import com.fast.dev.timercenter.server.core.dao.TaskTableDao;
import com.fast.dev.timercenter.server.core.helper.TaskPoolHelper;
import com.fast.dev.timercenter.service.type.TaskState;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log
@Service
public class TaskService {

    @Autowired
    private TaskTableDao taskTableDao;

    @Autowired
    private TimerCenterServerConf timerCenterServerConf;

    @Autowired
    private TaskPoolHelper taskPoolHelper;


    @Autowired
    private TimerServiceImpl timerService;

    /**
     * 执行任务
     */
    public void doTask() {
        this.taskTableDao.findAndUpdateTask(TaskState.Wait, TaskState.Work, timerCenterServerConf.getMaxExecuteTaskCount()).ifPresent((taskTables) -> {
            if (taskTables.size() > 0) {
                log.info("dotask : " + taskTables.size());
                taskTables.forEach((it) -> {
                    this.taskPoolHelper.execute(it);
                });
            }
        });
    }


    /**
     * 设置工作中的任务的心跳
     */
    public void setWorkTaskHeartbeat() {
        this.taskPoolHelper.getTaskIds().ifPresent((taskIds) -> {
            if (taskIds.size() > 0) {
                this.timerService.heartbeat(taskIds.toArray(new String[0]));
            }
        });
    }


}
