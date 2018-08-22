package com.fast.dev.ucenter.core.dao;

import com.fast.dev.ucenter.core.model.LoginEnvironment;
import com.fast.dev.ucenter.core.model.UserRegisterToken;
import com.fast.dev.ucenter.core.type.UserLoginType;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 *  用户令牌dao
 */
public interface UserTokenDao {


    public void getUserRegisterToken(UserLoginType userLoginType, String loginName, LoginEnvironment loginEnvironment);




}
