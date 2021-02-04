package com.fast.dev.auth.center.server.core.dao.impl;

import com.fast.dev.auth.center.server.core.dao.RoleDao;
import com.fast.dev.auth.center.server.core.dao.UserDao;
import com.fast.dev.auth.center.server.core.dao.extend.UserRoleDaoExtend;
import com.fast.dev.auth.center.server.core.domain.Role;
import com.fast.dev.auth.center.server.core.domain.User;
import com.fast.dev.auth.center.server.core.domain.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class UserRoleDaoImpl implements UserRoleDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserDao userDao;


    @Override
    public boolean existsByUserAndRole(String uid, String roleId) {

        Role role = this.roleDao.findTop1ById(roleId);
        if (role == null) {
            return false;
        }

        User user = this.userDao.findAndSaveUser(role.getEnterprise().getId(), uid);
        if (user == null) {
            return false;
        }

        return this.mongoTemplate.exists(Query.query(Criteria.where("user").is(user).and("role").is(role)), UserRole.class);
    }
}
