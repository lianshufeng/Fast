package com.fast.dev.auth.center.server.core.dao;

import com.fast.dev.auth.center.server.core.dao.extend.UserAuthDaoExtend;
import com.fast.dev.auth.center.server.core.domain.UserAuth;
import com.fast.dev.data.mongo.dao.MongoDao;

public interface UserAuthDao extends MongoDao<UserAuth>, UserAuthDaoExtend {

    /**
     * 通过用户中心的id查询到用户权限
     *
     * @param uid
     * @return
     */
    UserAuth findTop1ByUid(String uid);
}
