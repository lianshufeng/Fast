package com.fast.dev.ucenter.core.dao.impl;

import com.fast.dev.ucenter.core.dao.extend.BaseUserDaoExtend;
import com.fast.dev.ucenter.core.domain.BaseUser;
import com.fast.dev.ucenter.core.type.UserLoginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 */
public class BaseUserDaoImpl implements BaseUserDaoExtend {


    @Autowired
    private MongoTemplate mongoTemplate;


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
}
