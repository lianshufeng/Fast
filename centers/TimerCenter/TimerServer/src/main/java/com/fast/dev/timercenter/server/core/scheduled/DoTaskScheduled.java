package com.fast.dev.timercenter.server.core.scheduled;

import com.fast.dev.timercenter.server.core.service.TaskService;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
@Log
public class DoTaskScheduled {


    @Autowired
    private TaskService taskService;


    /**
     * 设置每条执行任务
     */

    @SneakyThrows
    @Scheduled(cron = "0/1 * * * * ?")
    public void doTask() {
        this.taskService.doTask();
    }

    /**
     * 设置当前正在执行的任务的心跳
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void setWorkTaskHeartbeat() {
        this.taskService.setWorkTaskHeartbeat();
    }


}
