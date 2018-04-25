package com.fast.dev.user.dao;

import com.fast.dev.component.mongodb.dao.MongoDao;
import com.fast.dev.user.domain.UserLog;
import org.springframework.stereotype.Component;
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
