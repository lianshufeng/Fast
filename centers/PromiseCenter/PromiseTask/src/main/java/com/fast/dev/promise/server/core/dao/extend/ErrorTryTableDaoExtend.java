package com.fast.dev.promise.server.core.dao.extend;

import com.fast.dev.promise.type.TaskState;

public interface ErrorTryTableDaoExtend {

    /**
     * 更新任务状态
     */
    boolean updateTaskState(String taskId, TaskState taskState);


}
