package com.fast.dev.auth.center.server.core.dao.impl;

import com.fast.dev.auth.center.server.core.dao.UserAuthDao;
import com.fast.dev.auth.center.server.core.dao.extend.UserAuthDaoExtend;
import com.fast.dev.auth.center.server.core.domain.UserAuth;
import com.fast.dev.data.mongo.helper.DBHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserAuthDaoImpl implements UserAuthDaoExtend {

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private UserAuthDao userAuthDao;

    @Override
    public boolean addAuth(String uid, String... authNames) {
        Query query = new Query();
        query.addCriteria(Criteria.where("uid").is(uid));


        Update update = new Update();
        update.setOnInsert("uid", uid);
        update.addToSet("auth").each(Arrays.stream(authNames).map((it) -> {
            return (Object) it;
        }).toArray());


        this.dbHelper.updateTime(update);
        return StringUtils.hasText(this.mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true).upsert(true), UserAuth.class).getUid());
    }

    @Override
    public boolean removeAuth(String uid, String... authNames) {
        Query query = new Query();
        query.addCriteria(Criteria.where("").is(null));


        Update update = new Update();
        update.setOnInsert("uid", uid);
        update.pullAll("auth", authNames);


        this.dbHelper.updateTime(update);
        return StringUtils.hasText(this.mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().returnNew(true).upsert(true), UserAuth.class).getUid());
    }

    @Override
    public boolean existAuth(String uid, String authName) {
        UserAuth userAuth = this.userAuthDao.findTop1ByUid(uid);
        if (userAuth == null) {
            return false;
        }
        if (userAuth.getAuth() != null) {
            return userAuth.getAuth().contains(authName);
        }
        return false;
    }

    @Override
    public Set<String> findUserAuth(String uid) {
        UserAuth userAuth = this.userAuthDao.findTop1ByUid(uid);
        if (userAuth == null) {
            return Set.of();
        }
        Set<String> auths = userAuth.getAuth();
        if (auths == null) {
            return Set.of();
        }
        return auths;
    }

}
