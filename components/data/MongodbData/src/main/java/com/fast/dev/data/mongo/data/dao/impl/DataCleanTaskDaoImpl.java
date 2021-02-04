package com.fast.dev.data.mongo.data.dao.impl;

import com.fast.dev.data.mongo.data.MongoDataCleanTask;
import com.fast.dev.data.mongo.data.dao.DataCleanTaskDao;
import com.fast.dev.data.mongo.data.domain.DataCleanTask;
import com.fast.dev.data.mongo.helper.DBHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class DataCleanTaskDaoImpl implements DataCleanTaskDao {
    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private MongoTemplate mongoTemplate;


    //任务超时时间
    private final static long TaskTimeOut = 10 * 1000l;

    @Override
    public boolean activeTask(String taskName) {
        Query query = Query.query(Criteria.where("taskName").is(taskName));
        Update update = new Update();
        update.set("activeTime", this.dbHelper.getTime());
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(query, update, DataCleanTask.class).getModifiedCount() > 0;
    }

    @Override
    public void setTaskprogress(String taskName, BigDecimal rate) {
        Update update = new Update();
        update.set("progress", rate);
        this.dbHelper.updateTime(update);
        this.mongoTemplate.updateFirst(Query.query(Criteria.where("taskName").is(taskName)), update, DataCleanTask.class);
    }

    /**
     * 创建任务
     *
     * @param mongoDataCleanTask
     */
    @Override
    public DataCleanTask executeTask(MongoDataCleanTask mongoDataCleanTask) {
        String uuid = UUID.randomUUID().toString();
        //查询条件
        Query query = Query.query(Criteria.where("taskName").is(mongoDataCleanTask.taskName()));

        //更新条件
        Update update = new Update();
        update.setOnInsert("taskName", mongoDataCleanTask.taskName());
        update.setOnInsert("entityName", mongoDataCleanTask.getEntity().getName());
        update.setOnInsert("limitUpdateTime", 0);
        update.setOnInsert("activeTime", this.dbHelper.getTime());
        update.setOnInsert("uuid", uuid);

        this.dbHelper.updateTime(update);
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        findAndModifyOptions.upsert(true);
        findAndModifyOptions.returnNew(true);

        DataCleanTask dataCleanTask = this.mongoTemplate.findAndModify(query, update, findAndModifyOptions, DataCleanTask.class);

        //判断是不是新增
        if (uuid.equalsIgnoreCase(dataCleanTask.getUuid())) {
            return dataCleanTask;
        }

        //超时才返回该任务对象
        if (dataCleanTask.getActiveTime() + TaskTimeOut < this.dbHelper.getTime()) {
            dataCleanTask.setActiveTime(this.dbHelper.getTime());
            this.mongoTemplate.save(dataCleanTask);
            return dataCleanTask;
        }
        return null;
    }


}
