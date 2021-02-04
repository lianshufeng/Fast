package com.fast.dev.auth.center.server.core.dao;

import com.fast.dev.auth.center.server.core.dao.extend.UserLogDaoExtend;
import com.fast.dev.auth.center.server.core.domain.UserLog;
import com.fast.dev.data.mongo.dao.MongoDao;

public interface UserLogDao extends MongoDao<UserLog>, UserLogDaoExtend {
}
