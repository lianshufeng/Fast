package com.fast.dev.ucenter.service.dao;

import com.fast.dev.component.mongodb.dao.MongoDao;
import com.fast.dev.ucenter.service.domain.UserLog;
import org.springframework.stereotype.Repository;

@Repository
public class UserLogDao extends MongoDao<UserLog> {

    public void insert(String userName){
        UserLog u = new UserLog();
        u.setName(userName);
        u.setTime(System.currentTimeMillis());
        this.mongoTemplate.save(u);
    }

}
