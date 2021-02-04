package com.fast.dev.auth.center.server.core.dao;

import com.fast.dev.auth.center.server.core.dao.extend.AuthNameDaoExtend;
import com.fast.dev.auth.center.server.core.domain.AuthName;
import com.fast.dev.data.mongo.dao.MongoDao;

public interface AuthNameDao extends MongoDao<AuthName>, AuthNameDaoExtend {


}
