package com.fast.dev.ucenter.core.service.extend;

import com.fast.dev.ucenter.core.model.LoginEnvironment;
import com.fast.dev.ucenter.core.model.UserRegisterToken;
import com.fast.dev.ucenter.core.type.TokenState;
import com.fast.dev.ucenter.core.type.UserLoginType;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 * <p>
 * 获取注册
 */
public interface UserRegister {


    /**
     * 获取注册令牌
     *
     * @param userLoginType
     * @param loginName
     * @param loginEnvironment
     * @return
     */
    public UserRegisterToken getUserRegisterToken(UserLoginType userLoginType, String loginName, LoginEnvironment loginEnvironment);


    /**
     * 注册
     *
     * @param token
     * @param code
     * @param passWord
     * @return
     */
    public TokenState register(String token, String code, String passWord);


}
