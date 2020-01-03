package com.fast.dev.robot.robotserver.core.dao.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.robot.robotserver.core.dao.extend.RobotRecordDaoExtend;
import com.fast.dev.robot.robotserver.core.domain.RobotRecord;
import com.fast.dev.robot.service.type.RobotType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Map;


public class RobotRecordDaoImpl implements RobotRecordDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;

    @Override
    public RobotRecord getRobotRecord(String token) {
        Query query = new Query();
        query.addCriteria(Criteria.where("token").is(token));
        query.limit(1);
        return this.mongoTemplate.findAndRemove(query, RobotRecord.class);
    }

    @Override
    public long cleanTimeOutRecord(Map<RobotType, Long> records) {
        BulkOperations bulkOperations = this.mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, RobotRecord.class);
        records.forEach((key, value) -> {
            Query query = new Query();
            //类型匹配 且 超时判断
            query.addCriteria(Criteria.where("robotType").is(key).and("createTime").lte(this.dbHelper.getTime() - value));
            bulkOperations.remove(query);
        });


        return bulkOperations.execute().getDeletedCount();
    }


}
