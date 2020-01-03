package com.fast.dev.promise.server.core.dao.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.promise.server.core.dao.extend.ErrorTryTableDaoExtend;
import com.fast.dev.promise.server.core.domain.ErrorTryTable;
import com.fast.dev.promise.server.core.domain.TaskTable;
import com.fast.dev.promise.type.TaskState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class ErrorTryTableDaoImpl implements ErrorTryTableDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;


    @Override
    public boolean updateTaskState(String taskId, TaskState taskState) {
        Update update = new Update();
        update.set("taskState", taskState);
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(new Query(Criteria.where("taskTable").is(TaskTable.build(taskId))), update, ErrorTryTable.class).getModifiedCount() > 0;
    }
}
