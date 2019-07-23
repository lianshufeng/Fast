package com.fast.dev.promise.server.core.scheduled;

import com.fast.dev.promise.server.core.service.TaskService;
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

    @Scheduled(cron = "0/1 * * * * ?")
    public void doTask() {
        this.taskService.doTask();
    }


    @Scheduled(cron = "0/3 * * * * ?")
    public void doErrorTask() {
        this.taskService.doErrorTask();
    }

}
