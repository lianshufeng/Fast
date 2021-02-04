package com.fast.dev.pay.server.core.hb.dao.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.server.core.hb.dao.exnted.HuaXiaEnterpriseTaskDaoExtend;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseAccount;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTask;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseTaskParameter;
import com.fast.dev.pay.server.core.hb.type.TaskState;
import com.fast.dev.pay.server.core.hb.type.TaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.UUID;

public class HuaXiaEnterpriseTaskDaoImpl implements HuaXiaEnterpriseTaskDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;

    @Override
    public String addTask(HuaXiaEnterpriseTask task) {

        //设置任务状态
        task.setState(TaskState.Process);

        this.dbHelper.saveTime(task);
        this.mongoTemplate.insert(task);

        return task.getId();
    }

    @Override
    public List<HuaXiaEnterpriseTask> findTask(HuaXiaEnterpriseAccount huaXiaEnterpriseAccount, TaskType taskType, long startWorkTime, long endWorkTime, long effectTime, int maxCount) {

        String session = UUID.randomUUID().toString();

        BulkOperations bulkOperations = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, HuaXiaEnterpriseTask.class);
        for (int i = 0; i < maxCount; i++) {

            Criteria criteria = new Criteria();

            //企业
            if (huaXiaEnterpriseAccount != null) {
                criteria = Criteria.where("huaXiaEnterpriseAccount").is(huaXiaEnterpriseAccount);
            }
            //任务类型
            criteria = criteria.and("type").is(taskType);
            //状态
            criteria = criteria.and("state").is(TaskState.Process);
            //生效时间
            criteria = criteria.and("effectTime").lte(effectTime);
            //过滤在此区间
            criteria = criteria.orOperator(
                    Criteria.where("taskProcess.workTime").exists(false),
                    Criteria.where("taskProcess.workTime").lt(startWorkTime),
                    Criteria.where("taskProcess.workTime").gt(endWorkTime)
            );


            Update update = new Update();
            update.set("taskProcess.session", session);
            update.set("taskProcess.workTime", this.dbHelper.getTime());

            //最后一次执行时间
            update.set("lastExecuteTime", this.dbHelper.getTime());

            //任务执行总次数
            update.inc("executeCount", 1);

            bulkOperations.updateOne(Query.query(criteria), update);
        }
        bulkOperations.execute();

        return this.mongoTemplate.find(Query.query(Criteria.where("taskProcess.session").is(session)), HuaXiaEnterpriseTask.class);
    }

    @Override
    public List<HuaXiaEnterpriseTask> findTask(TaskType taskType, long startWorkTime, long endWorkTime, long effectTime, int maxCount) {
        return findTask(null, taskType, startWorkTime, endWorkTime, effectTime, maxCount);
    }

    @Override
    public boolean updateTaskStateFinish(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("state", TaskState.Finish);
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(query, update, HuaXiaEnterpriseTask.class).getModifiedCount() > 0;
    }

    @Override
    public boolean updateTaskState(String id, TaskState state) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("state", state);
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(query, update, HuaXiaEnterpriseTask.class).getModifiedCount() > 0;
    }

    @Override
    public boolean updateParameter(String id, HuaXiaEnterpriseTaskParameter parameter) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("parameter", HuaXiaEnterpriseTask.toParameter(parameter));
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(query, update, HuaXiaEnterpriseTask.class).getModifiedCount() > 0;
    }

    @Override
    public long statisticsAmount(Long startTime, Long endTime, TaskType type) {

        int aomunt = 0;
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (startTime != null && endTime != null){
            criteria.and("createTime").gte(startTime).lte(endTime);
        } else if (startTime != null && endTime == null){
            criteria.and("createTime").gte(startTime);
        } else if (startTime == null && endTime != null){
            criteria.and("createTime").lte(endTime);
        }
        criteria.and("type").is(type).and("state").is(TaskState.Finish);
        query.addCriteria(criteria);
        List<HuaXiaEnterpriseTask> tasks = this.mongoTemplate.find(query,HuaXiaEnterpriseTask.class);
        if (tasks.size() > 0){
            for (HuaXiaEnterpriseTask task: tasks) {
                if (type == TaskType.Freeze){
                    aomunt = aomunt + (int)task.getParameter().get("amount");
                } else {
                    aomunt = aomunt + (int)task.getParameter().get("paymentAmount");
                }
            }
        }
        long result = Long.valueOf(aomunt/100);
        return result;
    }

    @Override
    public boolean existsByCheckTask(String chargeTaskId) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("parameter.chargeTaskId").is(chargeTaskId).and("type").is(TaskType.CheckCharge);
        query.addCriteria(criteria);

        return this.mongoTemplate.exists(query,HuaXiaEnterpriseTask.class);
    }

}
