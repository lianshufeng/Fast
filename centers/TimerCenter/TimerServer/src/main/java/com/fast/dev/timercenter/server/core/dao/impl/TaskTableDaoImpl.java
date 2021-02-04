package com.fast.dev.timercenter.server.core.dao.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.util.EntityObjectUtil;
import com.fast.dev.timercenter.server.core.dao.extend.TaskTableDaoExtend;
import com.fast.dev.timercenter.server.core.domain.TaskTable;
import com.fast.dev.timercenter.server.core.helper.TaskPoolHelper;
import com.fast.dev.timercenter.service.type.TaskState;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.*;

@Log
public class TaskTableDaoImpl implements TaskTableDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;


    @Autowired
    private TaskPoolHelper taskPoolHelper;


    @Override
    public Map<String, Boolean> heartbeatTime(String... taskId) {
        if (taskId == null || taskId.length == 0) {
            return new HashMap<>();
        }

        Query query = Query.query(EntityObjectUtil.createQueryBatch("taskId", taskId));
        Update update = new Update();
        update.set("heartbeatTime", this.dbHelper.getTime());
        this.dbHelper.updateTime(update);
        this.mongoTemplate.updateMulti(query, update, TaskTable.class);


        //查询数据状态
        Map<String, Boolean> ret = new HashMap<>();
        for (String id : taskId) {
            ret.put(id, false);
        }
        this.mongoTemplate.find(query, TaskTable.class).forEach((it) -> {
            ret.put(it.getTaskId(), true);
        });

        return ret;
    }

    @Override
    public Map<String, Boolean> removeTaskTableByTaskId(String... taskId) {
        Map<String, Boolean> ret = new HashMap<>();
        for (String id : taskId) {
            TaskTable taskTable = this.mongoTemplate.findAndRemove(new Query(Criteria.where("taskId").is(id)), TaskTable.class);
            ret.put(id, taskTable != null);
        }
        return ret;
    }


    @Override
    public Optional<List<TaskTable>> findAndUpdateTask(TaskState sourceState, TaskState targetState, long limitSize) {

        //防止当前执行的任务数过多
        if (this.taskPoolHelper.getTaskSize() >= limitSize) {
            return Optional.empty();
        }


        //创建资源令牌
        String uuid = UUID.randomUUID().toString();


        BulkOperations bulkOperations = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, TaskTable.class);
        for (int i = 0; i < limitSize; i++) {
            updateWorkTask(bulkOperations, uuid);
        }
        bulkOperations.execute();


        //查询刚更新过的数据
        return Optional.ofNullable(this.mongoTemplate.find(new Query(Criteria.where("resourceToken").is(uuid)), TaskTable.class));
    }


    @Override
    public Optional<TaskTable> removeByTaskId(String taskId) {
        return Optional.ofNullable(this.mongoTemplate.findAndRemove(Query.query(Criteria.where("taskId").is(taskId)), TaskTable.class));
    }


    /**
     * 更新任务状态为工作状态
     *
     * @param bulkOperations
     * @param uuid
     */
    private void updateWorkTask(BulkOperations bulkOperations, String uuid) {
        //心跳超时
        Criteria criteria = Criteria.where("$where").is(String.format("this.heartbeatTime + this.delayTime  < %s ", String.valueOf(this.dbHelper.getTime())));
        criteria = criteria.and("tryCount").gt(0);
        criteria = criteria.and("resourceToken").ne(uuid);
        Query query = new Query(criteria);

        Update update = new Update();
        update.set("resourceToken", uuid);
        update.set("heartbeatTime", this.dbHelper.getTime());
        update.inc("tryCount", -1);
        this.dbHelper.updateTime(update);

        bulkOperations.updateOne(query, update);
    }


}
