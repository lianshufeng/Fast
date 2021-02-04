package com.fast.dev.timercenter.server.core.dao;


import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.timercenter.server.core.dao.extend.TaskTableDaoExtend;
import com.fast.dev.timercenter.server.core.domain.TaskTable;

import java.util.Optional;

public interface TaskTableDao extends MongoDao<TaskTable>, TaskTableDaoExtend {


    /**
     * 通过任务id查找任务
     *
     * @param taskId
     * @return
     */
    TaskTable findFirstByTaskId(String taskId);



}
