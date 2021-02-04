package com.fast.dev.pay.server.core.hb.service.task;

import com.fast.dev.pay.server.core.hb.conf.HuaxiaConf;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.helper.TimeHelper;
import com.fast.dev.pay.server.core.hb.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class HuaXiaTaskExecuteManager {

    //线程池
    private ExecutorService threadPool = null;

    //任务
    private Map<String, HuaXiaTaskExecuteService> taskExecuteServiceMap = new ConcurrentHashMap<>();

    @Autowired
    private HuaxiaConf huaxiaConf;

    @PreDestroy
    private void shutdown() {
        if (threadPool != null) {
            threadPool.shutdownNow();
            this.threadPool = null;
        }
    }

    @Autowired
    private void init(ApplicationContext applicationContext) {
        this.threadPool = Executors.newFixedThreadPool(huaxiaConf.getTaskExecuteThreadCount());
        applicationContext.getBeansOfType(HuaXiaTaskExecuteService.class).values().forEach((it) -> {
            taskExecuteServiceMap.put(it.taskType().name(), it);
        });
    }


    /**
     * 执行任务
     *
     * @param taskType
     * @param account
     */
    public void executeTask(TaskType taskType, HuaXiaEnterpriseAccount account, TimeHelper.TimeType type) {
        HuaXiaTaskExecuteService huaXiaTaskExecuteService = taskExecuteServiceMap.get(taskType.name());
        threadPool.execute(() -> {
            huaXiaTaskExecuteService.execute(account, type);
        });
    }


    /**
     * 执行任务
     *
     * @param runnable
     */
    public void execute(Runnable runnable) {
        threadPool.execute(runnable);
    }


}
