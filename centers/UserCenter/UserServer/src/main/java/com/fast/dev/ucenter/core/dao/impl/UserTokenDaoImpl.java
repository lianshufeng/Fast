package com.fast.dev.ucenter.core.dao.impl;

import com.fast.dev.ucenter.core.dao.UserTokenDao;
import com.fast.dev.ucenter.core.model.LoginEnvironment;
import com.fast.dev.ucenter.core.type.UserLoginType;
import org.springframework.stereotype.Repository;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 */

@Repository
public class UserTokenDaoImpl implements UserTokenDao {


    @Override
    public void getUserRegisterToken(UserLoginType userLoginType, String loginName, LoginEnvironment loginEnvironment) {

    }
}
