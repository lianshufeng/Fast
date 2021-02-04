package com.fast.dev.auth.center.server.core.dao;

import com.fast.dev.auth.center.server.core.dao.extend.UserDaoExtend;
import com.fast.dev.auth.center.server.core.domain.User;
import com.fast.dev.data.mongo.dao.MongoDao;

public interface UserDao extends MongoDao<User>, UserDaoExtend {


}
