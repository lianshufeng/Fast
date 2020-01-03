package com.fast.dev.user.auth.service.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.user.auth.conf.UserRoleAuthConf;
import com.fast.dev.user.auth.domain.User;
import com.fast.dev.user.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


public class DefaultUserRoleAuthServiceImpl implements UserService<User> {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private UserRoleAuthConf userRoleAuthConf;


    @Override
    public User findAndSaveUser(String uid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("uid").is(uid));

        Update update = new Update();
        update.setOnInsert("uid", uid);
        this.dbHelper.saveTime(update);


        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        findAndModifyOptions.returnNew(true);
        findAndModifyOptions.upsert(true);

        return this.mongoTemplate.findAndModify(query, update, findAndModifyOptions, this.userRoleAuthConf.getUserEntityCls());
    }
}
