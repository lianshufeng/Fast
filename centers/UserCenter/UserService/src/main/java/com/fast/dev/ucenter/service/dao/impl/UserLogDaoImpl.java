package com.fast.dev.ucenter.service.dao.impl;

import com.fast.dev.ucenter.service.dao.extend.UserLogDaoExtend;
import com.fast.dev.ucenter.service.domain.UserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;

public class UserLogDaoImpl implements UserLogDaoExtend {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void updateUser(String name, long time) {
        Query query = new Query().addCriteria(Criteria.where("name").is(name));
        Update update = new Update();
        update.set("time",new BigDecimal(time));
        this.mongoTemplate.updateMulti(query,update,UserLog.class);
    }

    @Override
    public void mapReduce(String name) {
//        this.mongoTemplate.
    }
}
