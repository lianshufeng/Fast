package com.fast.dev.auth.center.server.core.dao;

import com.fast.dev.auth.center.server.core.dao.extend.UserRoleDaoExtend;
import com.fast.dev.auth.center.server.core.domain.Role;
import com.fast.dev.auth.center.server.core.domain.User;
import com.fast.dev.auth.center.server.core.domain.UserRole;
import com.fast.dev.data.mongo.dao.MongoDao;

public interface UserRoleDao extends MongoDao<UserRole>, UserRoleDaoExtend {

    boolean existsByUserAndRole(User user, Role role);

}
