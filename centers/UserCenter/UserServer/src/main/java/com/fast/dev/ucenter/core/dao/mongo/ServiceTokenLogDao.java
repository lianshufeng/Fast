package com.fast.dev.ucenter.core.dao.mongo;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.ucenter.core.dao.mongo.extend.ServiceTokenLogDaoExtend;
import com.fast.dev.ucenter.core.domain.ServiceTokenLog;

public interface ServiceTokenLogDao extends MongoDao<ServiceTokenLog>, ServiceTokenLogDaoExtend {
}
