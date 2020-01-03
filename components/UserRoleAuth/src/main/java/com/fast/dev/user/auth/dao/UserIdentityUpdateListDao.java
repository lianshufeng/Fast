package com.fast.dev.user.auth.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.user.auth.dao.extend.UserIdentityUpdateListDaoExtend;
import com.fast.dev.user.auth.domain.UserIdentityUpdateList;

public interface UserIdentityUpdateListDao extends MongoDao<UserIdentityUpdateList>, UserIdentityUpdateListDaoExtend {
}
