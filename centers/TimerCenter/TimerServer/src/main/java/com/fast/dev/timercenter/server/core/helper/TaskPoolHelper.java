package com.fast.dev.timercenter.server.core.helper;

import com.fast.dev.core.util.net.apache.HttpClientUtil;
import com.fast.dev.core.util.net.apache.ResponseModel;
import com.fast.dev.timercenter.server.core.conf.TimerCenterServerConf;
import com.fast.dev.timercenter.server.core.domain.TaskTable;
import com.fast.dev.timercenter.server.core.service.TimerServiceImpl;
import com.fast.dev.timercenter.server.core.util.TaskUtil;
import com.fast.dev.timercenter.service.model.TaskModel;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 任务池
 */

@Log
@Component
public class TaskPoolHelper {

    //任务集合
    private Vector<TaskTable> taskTables = new Vector<>();

    @Autowired
    private TimerCenterServerConf timerCenterServerConf;

    @Autowired
    private TaskPoolHelper taskPoolHelper;

    @Autowired
    private TimerServiceImpl timerService;

    //线程池
    ExecutorService threadPool;


    @Autowired
    private void init() {
        threadPool = Executors.newFixedThreadPool(this.timerCenterServerConf.getMaxExecuteTaskCount());
    }

    @PreDestroy
    private void shutdown() {
        threadPool.shutdownNow();
    }


    /**
     * 执行任务
     *
     * @param taskTable
     */
    public void execute(final TaskTable taskTable) {
        log.info("do task : " + taskTable.getId() + " -> " + taskTable.getHttpModel().getUrl());
        //增加任务队列
        this.taskTables.add(taskTable);
        this.threadPool.execute(() -> {
            try {
                //执行任务
                if (this.taskPoolHelper.doTask(taskTable)) {
                    this.timerService.remove(taskTable.getTaskId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //删除任务
            this.taskTables.remove(taskTable);
        });
    }


    /**
     * 获取当前任务列表中的id
     *
     * @return
     */
    public Optional<Set<String>> getTaskIds() {
        return Optional.ofNullable(this.taskTables.stream().map((it) -> {
            return it.getTaskId();
        }).collect(Collectors.toSet()));
    }


    /**
     * 获取当前任务列表的数量
     *
     * @return
     */
    public long getTaskSize() {
        return this.taskTables.size();
    }


    /**
     * 执行任务
     *
     * @param taskTable
     * @return
     */
    @Transactional
    @SneakyThrows
    public boolean doTask(TaskTable taskTable) {
        //转换到模型
        TaskModel taskModel = this.timerService.toTaskModel(taskTable);

        //进行网络请求
        ResponseModel responseModel = HttpClientUtil.request(taskModel.getHttp());

        // 检查结果是否成功
        return TaskUtil.validate(responseModel, taskModel.getCheck());
    }


}
