package com.fast.dev.promise.server.core.dao.extend;

import com.fast.dev.promise.server.core.domain.TaskTable;
import com.fast.dev.promise.type.TaskState;

import java.util.List;
import java.util.Map;

public interface TaskTableDaoExtend {


    boolean removeTaskTableByTaskId(String id);


    /**
     * 批量设置心跳时间
     *
     * @param id
     * @return
     */
    Map<String, Boolean> setHeartbeatTime(String... id);


    /**
     * 获取能执行的任务
     *
     * @param taskId
     * @return
     */
    TaskTable canDoTask(String taskId);


    /**
     * 查找超时的任务
     *
     * @return
     */
    List<TaskTable> findCanDoTask(int maxCount);


    /**
     * @param maxCount
     * @return
     */
    List<TaskTable> findCanDoErrorTask(int maxCount);


    /**
     * 设置任务状态
     *
     * @param taskId
     * @param taskState
     * @return
     */
    boolean setTaskState(String taskId, TaskState taskState);


    /**
     * 尝试执行任务
     *
     * @param id
     * @return
     */
    boolean setTryDoWorkById(String id);

}
