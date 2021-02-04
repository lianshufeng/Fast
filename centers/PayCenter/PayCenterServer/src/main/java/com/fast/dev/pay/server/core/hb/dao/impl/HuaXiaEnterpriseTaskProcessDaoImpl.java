package com.fast.dev.pay.server.core.hb.dao.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.util.EntityObjectUtil;
import com.fast.dev.pay.server.core.hb.dao.exnted.HuaXiaEnterpriseTaskProcessDaoExtend;
import com.fast.dev.pay.server.core.hb.domain.HuaXiaEnterpriseTaskProcess;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

import java.util.Date;

public class HuaXiaEnterpriseTaskProcessDaoImpl implements HuaXiaEnterpriseTaskProcessDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;

    //执行任务超时时间
    private long executeTimeout = 1000 * 60;


    @Override
    public boolean lockTask(String taskId) {
        Query query = Query.query(Criteria.where("taskId").is(taskId));

        Update update = new Update();
        update.set("TTL", new Date(this.dbHelper.getTime() + executeTimeout));
        update.setOnInsert("taskId", taskId);
        update.inc("count", 1);
        this.dbHelper.saveTime(update);

        HuaXiaEnterpriseTaskProcess taskProcess = this.mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true).returnNew(true), HuaXiaEnterpriseTaskProcess.class);
        return taskProcess.getCount() == 1;
    }

    @Override
    public void updateTTL(String... taskId) {
        Criteria criteria = EntityObjectUtil.createQueryBatch("taskId", taskId);
        Update update = new Update();
        update.set("TTL", new Date(this.dbHelper.getTime() + executeTimeout));
        this.dbHelper.updateTime(update);
        this.mongoTemplate.updateFirst(Query.query(criteria), update, HuaXiaEnterpriseTaskProcess.class);
    }

    @Override
    public long removeTask(String... taskId) {
        Criteria criteria = EntityObjectUtil.createQueryBatch("taskId", taskId);
        return this.mongoTemplate.remove(Query.query(criteria), HuaXiaEnterpriseTaskProcess.class).getDeletedCount();
    }


}
