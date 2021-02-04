package com.fast.dev.data.timer.core;

import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.data.timer.conf.TaskTimerConf;
import com.fast.dev.data.timer.domain.SimpleTaskTimerTable;
import com.fast.dev.data.timer.event.SimpleTaskTimerEvent;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;

/**
 * 任务调度器助手
 */
@Slf4j
@Component
@Scope("prototype")
public class SimpleTaskTimerCore {

    //任务定时器线程池
    private ExecutorService executorService = null;
    //数据库操作对象
    @Setter
    private SimpleTaskTimerDao taskTimerDao;

    //定时器触发事件
    @Setter
    private SimpleTaskTimerEvent<? extends SuperEntity> taskTimerEvent;

    /**
     * 动态调度器
     */
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TaskTimerConf taskTimerConf;


    //调度器任务
    private Map<String, TimerItem> scheduledFutureMap = new ConcurrentHashMap<>();


    @PreDestroy
    private void shutdown() {
        executorService.shutdownNow();
        scheduledFutureMap.values().forEach((it) -> {
            it.getScheduledFuture().cancel(true);
        });
        scheduledFutureMap.clear();
    }


    /**
     * 初始化
     */
    public void after() {
        executorService = Executors.newFixedThreadPool(taskTimerConf.getMaxThreadPoolCount());
        refreshTask();
        initListen();
    }


    /**
     * 监视数据库
     */
    private void initListen() {
        //获取需要监视的表
        final String collectionName = this.mongoTemplate.getCollectionName(this.taskTimerDao.getTaskTimerTableCls());

        //仅监视删除的数据
        this.executorService.execute(() -> {
            this.mongoTemplate.getCollection(collectionName).watch(new ArrayList<>() {{
                add(new Document("$match", new Document()));
            }}).forEach((it) -> {
                try {
                    watchDataUpdate(collectionName, it);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }


    /**
     * 监视数据更新
     *
     * @param collectionName
     * @param it
     */
    @SneakyThrows
    private void watchDataUpdate(String collectionName, ChangeStreamDocument it) {
        if (it.getOperationType() == OperationType.DROP || it.getOperationType() == OperationType.DROP_DATABASE) {
            this.cleanTask();
            return;
        }

        //取出任务变更的id
        String taskId = it.getDocumentKey().getObjectId("_id").getValue().toString();
        log.info("{} -> {} -> {}", collectionName, it.getOperationType(), taskId);
        if (it.getOperationType() == OperationType.DELETE) {
            this.deleteTask(taskId);
        } else {
            // insert update replace .....
            this.updateTask(this.taskTimerDao.findByIdToSimpleTaskTimerTable(taskId));
        }
    }

    /**
     * 刷新任务
     */
    public void refreshTask() {
        this.cleanTask();
        this.taskTimerDao.list().forEach((task) -> {
            this.updateTask(task);
        });
    }


    /**
     * 删除一个任务
     */
    private void deleteTask(final String taskId) {
        Optional.ofNullable(scheduledFutureMap.get(taskId)).ifPresent((it) -> {
            it.getScheduledFuture().cancel(true);
            scheduledFutureMap.remove(taskId);
        });
    }

    /**
     * 添加一个任务
     */
    private void addTask(final SimpleTaskTimerTable taskTimerTable) {
        if (!StringUtils.hasText(taskTimerTable.getCron())) {
            return;
        }
        //转换为表达式
        try {
            final CronTrigger cronTrigger = new CronTrigger(taskTimerTable.getCron());
            ScheduledFuture future = this.threadPoolTaskScheduler.schedule(() -> {
                //通知事件
                postEvent(taskTimerTable.getId());

            }, cronTrigger);
            this.scheduledFutureMap.put(taskTimerTable.getId(), new TimerItem()
                    .setCorn(taskTimerTable.getCron())
                    .setCreateTime(System.currentTimeMillis())
                    .setScheduledFuture(future)
            );
        } catch (Exception e) {
            log.error("e : {}", e);
        }
    }


    /**
     * 通知任务调度器被触发
     */
    private void postEvent(String taskId) {
        executorService.execute(() -> {
            taskTimerEvent.execute(taskTimerDao.findById(taskId));
        });
    }


    /**
     * 更新一个任务
     */
    private synchronized void updateTask(SimpleTaskTimerTable taskTimerTable) {
        //任务id
        String taskId = taskTimerTable.getId();
        //查询内存
        TimerItem timerItem = this.scheduledFutureMap.get(taskId);
        if (timerItem == null) {
            this.addTask(taskTimerTable);
            return;
        }

        // 调度器表达式未发生变化，则不处理调度功能
        if (timerItem.getCorn().equals(taskTimerTable.getCron())) {
            return;
        }


        //删除并重新增加
        this.deleteTask(taskId);
        this.addTask(taskTimerTable);

    }

    /**
     * 清空所有任务
     */
    private void cleanTask() {
        scheduledFutureMap.keySet().forEach((it) -> {
            this.deleteTask(it);
        });
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    private static class TimerItem {
        //表达式
        private String corn;
        //创建时间
        private long createTime;
        //调度任务
        private ScheduledFuture scheduledFuture;
    }

}
