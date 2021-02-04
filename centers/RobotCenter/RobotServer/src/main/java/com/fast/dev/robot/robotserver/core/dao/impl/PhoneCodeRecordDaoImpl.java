package com.fast.dev.robot.robotserver.core.dao.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.robot.robotserver.core.dao.extend.PhoneCodeRecordDaoExtend;
import com.fast.dev.robot.robotserver.core.domain.PhoneCodeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class PhoneCodeRecordDaoImpl implements PhoneCodeRecordDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;

    @Override
    public PhoneCodeRecord queryAndIncTryCount(String token) {

        Query query = Query.query(Criteria.where("token").is(token).and("tryCount").gt(0));
        Update update = new Update();
        update.inc("tryCount", -1);
        this.dbHelper.updateTime(update);

        return this.mongoTemplate.findAndModify(query, update, PhoneCodeRecord.class);
    }
}
