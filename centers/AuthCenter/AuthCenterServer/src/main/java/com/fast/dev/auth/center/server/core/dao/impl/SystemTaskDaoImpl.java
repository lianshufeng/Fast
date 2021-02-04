package com.fast.dev.auth.center.server.core.dao.impl;

import com.fast.dev.auth.center.server.core.dao.extend.SystemTaskDaoExtend;
import com.fast.dev.auth.center.server.core.domain.SystemTask;
import com.fast.dev.data.mongo.helper.DBHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;

public class SystemTaskDaoImpl implements SystemTaskDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;

    @Override
    public Long task(String taskName, Long ttlTime) {

        Query query = new Query(Criteria.where("taskName").is(taskName));

        Update update = new Update();
        update.inc("count", 1);
        update.setOnInsert("ttl", new Date(this.dbHelper.getTime() + ttlTime));
        this.dbHelper.saveTime(update);

        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        findAndModifyOptions.upsert(true);
        findAndModifyOptions.returnNew(true);

        SystemTask systemTask = this.mongoTemplate.findAndModify(query, update, findAndModifyOptions, SystemTask.class);
        return systemTask.getCount();
    }

    @Override
    public void finish(String taskName) {
        this.mongoTemplate.remove(Query.query(Criteria.where("taskName").is(taskName)), SystemTask.class);
    }
}
