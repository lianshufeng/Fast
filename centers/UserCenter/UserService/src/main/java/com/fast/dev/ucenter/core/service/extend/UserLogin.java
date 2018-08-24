package com.fast.dev.ucenter.core.service.extend;

import com.fast.dev.ucenter.core.model.LoginEnvironment;
import com.fast.dev.ucenter.core.model.UserLoginToken;
import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.core.type.TokenState;
import com.fast.dev.ucenter.core.type.UserLoginType;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 */
public interface UserLogin {


    /**
     * 获取用户登陆令牌
     *
     * @return
     */
    public UserLoginToken getUserLoginToken(UserLoginType userLoginType, String loginName,LoginEnvironment loginEnvironment);


    /**
     * 登陆
     *
     * @param token
     * @param validateCode
     * @param passWord
     */
    public UserTokenModel login(String token, String validateCode, String passWord, long timeOut);


    /**
     * 注销
     *
     * @param token
     */
    public TokenState logout(String token);


}
