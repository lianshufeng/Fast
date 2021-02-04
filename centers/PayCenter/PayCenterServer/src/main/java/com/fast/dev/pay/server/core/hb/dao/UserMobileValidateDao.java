package com.fast.dev.pay.server.core.hb.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.pay.server.core.hb.domain.UserMobileValidate;

public interface UserMobileValidateDao extends MongoDao<UserMobileValidate> {

    UserMobileValidate findByIdAndCode(String id, String code);

    long removeByIdAndCode(String id, String code);
}
