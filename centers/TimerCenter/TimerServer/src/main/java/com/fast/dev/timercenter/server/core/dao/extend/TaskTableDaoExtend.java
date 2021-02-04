package com.fast.dev.timercenter.server.core.dao.extend;


import com.fast.dev.timercenter.server.core.domain.TaskTable;
import com.fast.dev.timercenter.service.type.TaskState;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TaskTableDaoExtend {


    /**
     * 设置心跳任务
     *
     * @param taskId
     * @return
     */
    Map<String, Boolean> heartbeatTime(String... taskId);


    /**
     * 删除任务
     *
     * @return
     */
    Map<String, Boolean> removeTaskTableByTaskId(String... taskId);


    /***
     * 查找任务
     * @param sourceState
     * @param targetState
     * @param limitSize
     * @return
     */
    Optional<List<TaskTable>> findAndUpdateTask(TaskState sourceState, TaskState targetState, long limitSize);


    /**
     * 通过任务id删除任务并返回
     *
     * @param taskId
     * @return
     */
    Optional<TaskTable> removeByTaskId(String taskId);

}
