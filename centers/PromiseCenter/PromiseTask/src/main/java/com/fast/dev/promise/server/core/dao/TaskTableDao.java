package com.fast.dev.promise.server.core.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.promise.server.core.dao.extend.TaskTableDaoExtend;
import com.fast.dev.promise.server.core.domain.TaskTable;

public interface TaskTableDao extends MongoDao<TaskTable>, TaskTableDaoExtend {


    /**
     * 通过任务id查找任务
     *
     * @param taskId
     * @return
     */
    TaskTable findFirstByTaskId(String taskId);


}
