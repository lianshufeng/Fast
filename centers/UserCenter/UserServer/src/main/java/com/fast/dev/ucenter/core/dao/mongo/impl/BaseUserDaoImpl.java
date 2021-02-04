package com.fast.dev.ucenter.core.dao.mongo.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.data.mongo.util.EntityObjectUtil;
import com.fast.dev.ucenter.core.dao.mongo.BaseUserLogDao;
import com.fast.dev.ucenter.core.dao.mongo.extend.BaseUserDaoExtend;
import com.fast.dev.ucenter.core.domain.BaseUser;
import com.fast.dev.ucenter.core.domain.BaseUserLog;
import com.fast.dev.ucenter.core.model.UserDisableModel;
import com.fast.dev.ucenter.core.type.UserLoginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.*;


/**
 * 作者：练书锋
 * 时间：2018/8/22
 */
public class BaseUserDaoImpl implements BaseUserDaoExtend {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private BaseUserLogDao baseUserLogDao;


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
        update.set("salt", salt);
        update.set("passWord", passWord);
        update.unset("passWordEncodeType");
        this.dbHelper.updateTime(update);
        return this.mongoTemplate.updateFirst(query, update, BaseUser.class).getModifiedCount() > 0;
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

        update.set(loginType.getUserLoginTypeName(), loginName);

        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(false);
        options.returnNew(true);

        //记录更新日志
        recordUpdateLoginValueLog(uid, loginType, loginName);


        return this.mongoTemplate.findAndModify(query, update, options, BaseUser.class);
    }


    @Override
    public long updateUserDisable(UserDisableModel userDisableModel, String... uid) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(uid));

        Update update = new Update();
        update.set("userDisable", userDisableModel);
        this.dbHelper.updateTime(update);

        return this.mongoTemplate.updateMulti(query, update, BaseUser.class).getModifiedCount();
    }

    @Override
    public List<BaseUser> batchQuery(Collection<Map<String, Object>> items) {
        return this.dbHelper.batchQuery(BaseUser.class, items);
    }


    /**
     * 记录更换用户的登录名的日志
     *
     * @param uid
     * @param loginType
     * @param loginName
     */
    private void recordUpdateLoginValueLog(String uid, UserLoginType loginType, String loginName) {

        BaseUserLog baseUserLog = new BaseUserLog();
        BaseUser baseUser = new BaseUser();
        baseUser.setId(uid);
        baseUserLog.setBaseUser(baseUser);
        baseUserLog.setUserLoginType(loginType);
        baseUserLog.setLoginName(loginName);

        this.dbHelper.saveTime(baseUserLog);

        this.baseUserLogDao.save(baseUserLog);

    }


}
