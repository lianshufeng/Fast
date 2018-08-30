package com.fast.dev.ucenter.core.service.extend;

import com.fast.dev.ucenter.core.model.TokenEnvironment;
import com.fast.dev.ucenter.core.model.UserLoginToken;
import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.core.type.UserLoginType;
import org.springframework.web.bind.annotation.RequestParam;

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
    public UserLoginToken getUserLoginToken(UserLoginType userLoginType, String loginName, @RequestParam TokenEnvironment tokenEnvironment);


    /**
     * 登陆
     *
     * @param token
     * @param validateCode
     * @param passWord
     */
    public UserTokenModel login(TokenEnvironment env, String token, String validateCode, String passWord, long expireTime);


    /**
     * 注销
     *
     * @param token
     */
    public boolean logout(String token);


}
