package com.fast.dev.ucenter.service.extend;

import com.fast.dev.ucenter.model.LoginEnvironment;
import com.fast.dev.ucenter.model.UserLoginToken;
import com.fast.dev.ucenter.model.UserToken;
import com.fast.dev.ucenter.type.UserLoginType;

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
    public UserLoginToken getUserLoginToken(LoginEnvironment loginEnvironment, UserLoginType userLoginType, String loginName);


    /**
     * 登陆
     *
     * @param token
     * @param validateCode
     * @param passWord
     */
    public UserToken login(String token, String validateCode, String passWord);


    /**
     * 注销
     *
     * @param token
     */
    public void logout(String token);


}
