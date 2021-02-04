package com.fast.dev.robot.robotserver.core.scheduled;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.robot.robotserver.core.domain.PhoneCodeRecord;
import com.fast.dev.robot.robotserver.core.domain.RobotRecord;
import com.fast.dev.robot.robotserver.core.domain.TokenRecord;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 清除过期数据
 */
@Log
@Component
@EnableScheduling
public class CleanTimeOutRecordsScheduled {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;


    private Class<? extends TokenRecord>[] entityCls = new Class[]{
            RobotRecord.class,
            PhoneCodeRecord.class
    };

    @Autowired
    private void init() {

    }


    @Scheduled(cron = "*/30 * * * * ?")
    public void cleanTimeOutToken() {
        for (Class<? extends TokenRecord> cls : entityCls) {
            Query query = Query.query(Criteria.where("expireTime").lt(dbHelper.getTime()));
            long count = this.mongoTemplate.remove(query, cls).getDeletedCount();
            if (count > 0) {
                log.info(String.format("clean %s token : %s", cls.getName(), String.valueOf(count)));
            }
        }
    }




}
