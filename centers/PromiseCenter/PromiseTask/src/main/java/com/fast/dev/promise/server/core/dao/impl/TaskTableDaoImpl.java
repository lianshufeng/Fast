package com.fast.dev.promise.server.core.dao.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.util.EntityObjectUtil;
import com.fast.dev.promise.server.core.dao.ErrorTryTableDao;
import com.fast.dev.promise.server.core.dao.extend.TaskTableDaoExtend;
import com.fast.dev.promise.server.core.domain.ErrorTryTable;
import com.fast.dev.promise.server.core.domain.TaskTable;
import com.fast.dev.promise.type.TaskState;
import lombok.extern.java.Log;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log
public class TaskTableDaoImpl implements TaskTableDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;


    @Autowired
    private ErrorTryTableDao errorTryTableDao;


    @Override
    public boolean removeTaskTableByTaskId(String id) {
        TaskTable taskTable = this.mongoTemplate.findAndRemove(createQueryTaskByTaskId(id), TaskTable.class);
        if (taskTable == null) {
            return false;
        }
        //删除对应的数据
        this.mongoTemplate.remove(taskTable.getHttpTable());
        this.mongoTemplate.remove(taskTable.getErrorTryTable());
        return true;
    }


    @Override
    public Map<String, Boolean> setHeartbeatTime(String... id) {
        long time = this.dbHelper.getTime();
        Criteria criteria = EntityObjectUtil.createQueryBatch("taskId", id);
        Update update = new Update();
        update.set("heartbeatTime", time);
        this.dbHelper.updateTime(update);

        //批量心跳
        this.mongoTemplate.updateMulti(new Query(criteria), update, TaskTable.class);

        Map<String, Boolean> ret = new HashMap<>();
        for (String i : id) {
            ret.put(i, false);
        }
        //查询修改成功的数据
        for (TaskTable taskTable : this.mongoTemplate.find(new Query(criteria.and("heartbeatTime").is(time)), TaskTable.class)) {
            ret.put(taskTable.getTaskId(), true);
        }
        return ret;
    }

    @Override
    public TaskTable canDoTask(String taskId) {

        Query query = new Query();
        query.addCriteria(Criteria.where("taskState").is(TaskState.Wait).and("taskId").is(taskId));
        query.limit(1);

        Update update = new Update();
        updateTaskState(update, TaskState.Work);

        return this.mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), TaskTable.class);

    }

    @Override
    public List<TaskTable> findCanDoTask(int maxCount) {
        List<TaskTable> list = new ArrayList<>();
        for (int i = 0; i < maxCount; i++) {
            TaskTable taskTable = findCanDoTask();
            if (taskTable != null) {
                list.add(taskTable);
            } else {
                break;
            }
        }


        return list;
    }


    /**
     * 更新错误数据
     *
     * @param taskId
     * @param taskState
     */
    private void updateErrorTaskState(String taskId, TaskState taskState) {
        this.errorTryTableDao.updateTaskState(taskId, taskState);
    }


    /**
     * 更新任务状态
     *
     * @param update
     * @param taskState
     */
    private void updateTaskState(Update update, TaskState taskState) {
        update.set("taskState", taskState);
        this.dbHelper.updateTime(update);
    }


    /**
     * 查询一个错误且可执行的数据
     *
     * @return
     */
    private ErrorTryTable findCanDoErrorTask() {
        Document document = new Document();
        document.put("$where", "this.tryTime + this.sleepTime " + " < " + (this.dbHelper.getTime()));
        document.put("tryCount", new Document("$gt", 0));
        document.put("taskState", TaskState.Work);

        Query query = new BasicQuery(document);
        query.limit(1);

        Update update = new Update();
        update.set("tryTime", this.dbHelper.getTime());
        this.dbHelper.updateTime(update);

        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        findAndModifyOptions.returnNew(true);
        ErrorTryTable errorTryTable = this.mongoTemplate.findAndModify(query, update, findAndModifyOptions, ErrorTryTable.class);
        if (errorTryTable != null) {
            setTryDoWorkById(errorTryTable.getTaskTable().getId());
        }
        return errorTryTable;

    }

    @Override
    public List<TaskTable> findCanDoErrorTask(int maxCount) {


        //ToDo 需要设置失败开始策略 且有优化空间
        List<TaskTable> list = new ArrayList<>();
        for (int i = 0; i < maxCount; i++) {

            ErrorTryTable errorTryTable = findCanDoErrorTask();
            //如果未找到错误的任务则跳出查找
            if (errorTryTable == null) {
                break;
            }

            TaskTable taskTable = errorTryTable.getTaskTable();
            //如果没有关联到任务则删除重试记录
            if (taskTable == null) {
                this.mongoTemplate.remove(errorTryTable);
                continue;
            }
            list.add(taskTable);

        }
        return list;
    }


    /**
     * 查询可以执行的一条任务
     *
     * @return
     */
    private TaskTable findCanDoTask() {
        Document document = new Document();
        document.put("$where", "this.heartbeatTime + this.executeTime < " + this.dbHelper.getTime());
        document.put("taskState", TaskState.Wait);
        Query query = new BasicQuery(document);
        query.limit(1);
        Update update = new Update();
        updateTaskState(update, TaskState.Work);

        TaskTable taskTable = this.mongoTemplate.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), TaskTable.class);

        //标记该任务将要被执行
        if (taskTable != null) {
            setTryDoWorkById(taskTable.getId());
        }
        return taskTable;
    }


    @Override
    public boolean setTaskState(String taskId, TaskState taskState) {
        Update update = new Update();
        update.set("taskState", taskState);
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(createQueryTaskByTaskId(taskId), update, TaskTable.class).getModifiedCount() > 0;
    }

    @Override
    public boolean setTryDoWorkById(String id) {
        Query query = new Query();

        TaskTable taskTable = new TaskTable();
        taskTable.setId(id);
        query.addCriteria(Criteria.where("taskTable").is(taskTable));

        Update update = new Update();
        update.inc("tryCount", -1);
        update.set("tryTime", this.dbHelper.getTime());
        update.set("taskState", TaskState.Work);

        this.dbHelper.updateTime(update);

        return this.mongoTemplate.updateFirst(query, update, ErrorTryTable.class).getModifiedCount() > 0;

    }

    /**
     * 构建查询条件
     *
     * @param taskId
     * @return
     */
    private Query createQueryTaskByTaskId(String taskId) {
        return new Query().addCriteria(Criteria.where("taskId").is(taskId));
    }


}
