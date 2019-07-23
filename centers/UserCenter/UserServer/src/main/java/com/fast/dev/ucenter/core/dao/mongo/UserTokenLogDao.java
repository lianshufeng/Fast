package com.fast.dev.ucenter.core.dao.mongo;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.ucenter.core.dao.mongo.extend.UserTokenLogDaoExtend;
import com.fast.dev.ucenter.core.domain.UserTokenLog;

public interface UserTokenLogDao extends MongoDao<UserTokenLog>, UserTokenLogDaoExtend {
}
