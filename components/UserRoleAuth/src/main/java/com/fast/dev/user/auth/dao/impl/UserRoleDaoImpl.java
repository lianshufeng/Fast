package com.fast.dev.user.auth.dao.impl;

import com.fast.dev.user.auth.dao.RoleDao;
import com.fast.dev.user.auth.dao.extend.UserRoleDaoExtend;
import com.fast.dev.user.auth.domain.Role;
import com.fast.dev.user.auth.domain.User;
import com.fast.dev.user.auth.domain.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class UserRoleDaoImpl implements UserRoleDaoExtend {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RoleDao roleDao;

    @Override
    public boolean existsByUserAndRole(String userId, String roleName) {

        User user = new User();
        user.setId(userId);



        Role role = this.roleDao.findByRoleName(roleName);
        if (role == null) {
            return false;
        }


        return this.mongoTemplate.exists(Query.query(Criteria.where("user").is(user).and("role").is(role)), UserRole.class);
    }
}
