package com.fast.dev.auth.center.server.core.service.impl;

import com.fast.dev.auth.center.server.core.annotations.CleanUserCache;
import com.fast.dev.auth.center.server.core.dao.UserAuthDao;
import com.fast.dev.auth.center.server.core.type.CleanUserCacheType;
import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import com.fast.dev.auth.client.log.annotations.UserLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAuthServiceImpl {

    @Autowired
    private UserAuthDao userAuthDao;

    /**
     * 添加用户的权限
     *
     * @param uid
     * @return
     */

    @Transactional
    @UserLog(value = ResourceAuthConstant.Manager, parameter = {"#uid","#auth"})
    @CleanUserCache(value = "#uid", type = CleanUserCacheType.User)
    public boolean add(String uid, String... auth) {
        return userAuthDao.addAuth(uid, auth);
    }


    /**
     * 删除用户的权限
     *
     * @param uid
     * @return
     */
    @Transactional
    @UserLog(value = ResourceAuthConstant.Manager, parameter = {"#uid","#auth"})
    @CleanUserCache(value = "#uid", type = CleanUserCacheType.User)
    public boolean remove(String uid, String... auth) {
        return this.userAuthDao.removeAuth(uid, auth);
    }

}
