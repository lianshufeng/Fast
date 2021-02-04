package com.fast.dev.ucenter.core.service;

import com.fast.dev.ucenter.core.model.BasicServiceToken;
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
    boolean ping(String uToken);


    /**
     * 加强校验令牌
     *
     * @param token
     * @param code
     * @return
     */
    BasicServiceToken strongToken(String token, String code);


}
