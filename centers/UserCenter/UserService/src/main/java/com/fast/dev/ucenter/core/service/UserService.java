package com.fast.dev.ucenter.core.service;

import com.fast.dev.ucenter.core.service.extend.UserInfo;
import com.fast.dev.ucenter.core.service.extend.UserLogin;
import com.fast.dev.ucenter.core.service.extend.UserPassword;
import com.fast.dev.ucenter.core.service.extend.UserRegister;

/**
 * 用户业务
 */
public interface UserService extends UserLogin, UserInfo, UserPassword, UserRegister {


    /**
     * 测试当前用户令牌是否生效
     *
     * @param uToken
     * @return
     */
    public boolean ping(String uToken);


}
