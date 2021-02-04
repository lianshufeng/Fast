package com.fast.dev.auth.center.server.core.dao;

import com.fast.dev.auth.center.server.core.dao.extend.IdentityNameDaoExtend;
import com.fast.dev.auth.center.server.core.domain.IdentityName;
import com.fast.dev.data.mongo.dao.MongoDao;

public interface IdentityNameDao extends MongoDao<IdentityName>, IdentityNameDaoExtend {

}
