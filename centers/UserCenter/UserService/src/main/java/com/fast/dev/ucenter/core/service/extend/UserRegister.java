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


    public UserRegisterToken getUserRegisterToken(LoginEnvironment loginEnvironment, UserLoginType userLoginType, String loginName);


    public TokenState register(String token, String code, String passWord);


}
