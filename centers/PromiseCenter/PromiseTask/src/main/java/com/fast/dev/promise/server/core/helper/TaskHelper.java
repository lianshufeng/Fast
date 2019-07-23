package com.fast.dev.promise.server.core.helper;

import com.fast.dev.core.util.net.apache.HttpClientUtil;
import com.fast.dev.core.util.net.apache.ResponseModel;
import com.fast.dev.promise.model.ErrorTryModel;
import com.fast.dev.promise.model.TaskModel;
import com.fast.dev.promise.server.core.config.PromiseTaskConfig;
import com.fast.dev.promise.server.core.domain.TaskTable;
import com.fast.dev.promise.server.core.service.TaskService;
import com.fast.dev.promise.server.core.util.TaskUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log
@Component
public class TaskHelper {
    //线程池
    ExecutorService threadPool;


    @Autowired
    private PromiseTaskConfig promiseTaskConfig;

    @Autowired
    private TaskService taskService;


    /**
     * 当前任务数量
     */
    private int currentTaskCount = 0;


    @Autowired
    private void init() {
        threadPool = Executors.newFixedThreadPool(promiseTaskConfig.getMaxExecuteTaskCount());
    }


    /**
     * 开始执行任务
     */
    public void doit(final TaskTable taskTable) {
        this.threadPool.execute(new TaskRunnable(taskTable));
    }


    /**
     * 增加添加任务当前类型
     *
     * @param val
     */
    protected synchronized void incCurrentTaskCount(int val) {
        currentTaskCount += val;
    }

    /**
     * 获取当前任务的数量
     *
     * @return
     */
    public int getCurrentTaskCount() {
        return currentTaskCount;
    }

    /**
     * 执行任务
     *
     * @param taskTable
     * @return
     */

    private boolean doTask(TaskTable taskTable) {
        try {

            //转换到模型
            TaskModel taskModel = this.taskService.taskTable2Model(taskTable);

            //进行网络请求
            ResponseModel responseModel = HttpClientUtil.request(taskModel.getHttp());


            //检查结果是否成功
            ErrorTryModel errorTryModel = taskModel.getErrorTry();

            //对请求数据后进行验证
            return TaskUtil.validate(responseModel, errorTryModel);

        } catch (Exception e) {
            log.info("do task error :" + e.getMessage());
        }
        return false;
    }


    @PreDestroy
    private void shutdown() {
        this.threadPool.shutdownNow();
    }


    /**
     * 执行任务的线程
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class TaskRunnable implements Runnable {

        private TaskTable taskTable;

        @Override
        public void run() {

            //当前任务+1
            incCurrentTaskCount(1);

            try {

                //执行任务
                boolean ret = doTask(taskTable);

                //任务执行成功则删除该任务
                if (ret) {
                    taskService.removeTaskTableByTaskId(taskTable.getTaskId());
                }

                log.info("任务: " + this.taskTable.getTaskId() + " -> " + ret);

            } catch (Exception e) {
                e.printStackTrace();
            }

            //当前任务-1
            incCurrentTaskCount(-1);


        }
    }


}
