package com.fast.dev.data.mongo.data.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.data.mongo.data.dao.extend.DataCleanTaskDaoExtend;
import com.fast.dev.data.mongo.data.domain.DataCleanTask;

public interface DataCleanTaskDao extends MongoDao<DataCleanTask>, DataCleanTaskDaoExtend {


    /**
     * 通过任务名查询该任务
     *
     * @param taskName
     * @return
     */
    DataCleanTask findByTaskName(String taskName);


}
