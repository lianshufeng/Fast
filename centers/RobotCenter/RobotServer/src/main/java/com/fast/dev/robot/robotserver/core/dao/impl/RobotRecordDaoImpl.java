package com.fast.dev.robot.robotserver.core.dao.impl;

import com.fast.dev.robot.robotserver.core.dao.extend.RobotRecordDaoExtend;
import com.fast.dev.robot.robotserver.core.domain.RobotRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;


public class RobotRecordDaoImpl implements RobotRecordDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RobotRecord getRobotRecord(String token) {
        Query query = new Query();
        query.addCriteria(Criteria.where("token").is(token));
        query.limit(1);
        return this.mongoTemplate.findAndRemove(query, RobotRecord.class);
    }
}
