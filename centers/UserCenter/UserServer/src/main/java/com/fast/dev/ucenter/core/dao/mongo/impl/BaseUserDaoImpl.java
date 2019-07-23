package com.fast.dev.ucenter.core.dao.mongo.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.ucenter.core.dao.mongo.extend.BaseUserDaoExtend;
import com.fast.dev.ucenter.core.domain.BaseUser;
import com.fast.dev.ucenter.core.type.UserLoginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 */
public class BaseUserDaoImpl implements BaseUserDaoExtend {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;


    @Override
    public String queryUserSalt(UserLoginType userLoginType, String loginName) {
        Criteria criteria = null;
        if (userLoginType == UserLoginType.Phone) {
            criteria = Criteria.where("phone");
        } else if (userLoginType == UserLoginType.UserName) {
            criteria = Criteria.where("userName");
        }
        if (criteria == null) {
            return null;
        }
        Query query = new Query(criteria.is(loginName));
        BaseUser baseUser = this.mongoTemplate.findOne(query, BaseUser.class);
        if (baseUser == null) {
            return null;
        }
        return baseUser.getSalt();
    }


    @Override
    public BaseUser findAndSaveBaseUser(String phone) {
        //查询用户手机号码
        Query query = new Query().addCriteria(Criteria.where("phone").is(phone));

        //若没有找到改用户则创建新用户
        Update update = new Update();
        update.setOnInsert("phone", phone);
        update.setOnInsert("_class", BaseUser.class.getName());
        this.dbHelper.saveTime(update);

        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        options.upsert(true);

        return this.mongoTemplate.findAndModify(query, update, options, BaseUser.class);
    }

    @Override
    public BaseUser queryByLoginName(UserLoginType loginType, String loginName) {
        Query query = new Query().addCriteria(Criteria.where(loginType.getUserLoginTypeName()).is(loginName));
        return this.mongoTemplate.findOne(query, BaseUser.class);
    }

    @Override
    public boolean updatePassWord(String id, String salt, String passWord) {
        //查询用户手机号码
        Query query = new Query().addCriteria(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("salt",salt);
        update.set("passWord",passWord);
        update.unset("passWordEncodeType");
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(query,update,BaseUser.class).getModifiedCount()>0;
    }

    @Override
    public boolean existsByLoginName(UserLoginType loginType, String loginName) {
        Query query = new Query().addCriteria(Criteria.where(loginType.getUserLoginTypeName()).is(loginName));
        return this.mongoTemplate.exists(query, BaseUser.class);
    }

    @Override
    public BaseUser updateLoginValue(String uid, UserLoginType loginType, String loginName) {
        Query query = new Query().addCriteria(Criteria.where("_id").is(uid));
        Update update = new Update();

        update.set(loginType.getUserLoginTypeName(),loginName);

        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(false);
        options.returnNew(true);

        return this.mongoTemplate.findAndModify(query,update,options,BaseUser.class);
    }


}
