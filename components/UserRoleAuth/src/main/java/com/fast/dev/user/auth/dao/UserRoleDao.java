package com.fast.dev.user.auth.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.user.auth.dao.extend.UserRoleDaoExtend;
import com.fast.dev.user.auth.domain.UserRole;

public interface UserRoleDao extends MongoDao<UserRole>, UserRoleDaoExtend {


}
