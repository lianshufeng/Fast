package com.fast.dev.ucenter.boot.service;

import com.fast.dev.ucenter.model.LoginEnvironment;
import com.fast.dev.ucenter.model.UserLoginToken;
import com.fast.dev.ucenter.model.UserToken;
import com.fast.dev.ucenter.service.UserService;
import com.fast.dev.ucenter.type.UserLoginType;
import org.springframework.stereotype.Service;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 */
@Service
public class UserServiceImpl implements UserService {
    @Override
    public UserLoginToken getUserLoginToken(LoginEnvironment loginEnvironment, UserLoginType userLoginType, String loginName) {
        return null;
    }

    @Override
    public UserToken login(String token, String validateCode, String passWord) {
        return null;
    }

    @Override
    public void logout(String token) {

    }
}
