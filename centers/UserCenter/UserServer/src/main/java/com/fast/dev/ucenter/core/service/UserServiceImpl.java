package com.fast.dev.ucenter.core.service;

import com.fast.dev.ucenter.core.dao.UserBaseDao;
import com.fast.dev.ucenter.core.model.LoginEnvironment;
import com.fast.dev.ucenter.core.model.UserLoginToken;
import com.fast.dev.ucenter.core.model.UserRegisterToken;
import com.fast.dev.ucenter.core.model.UserToken;
import com.fast.dev.ucenter.core.type.TokenState;
import com.fast.dev.ucenter.core.type.UserLoginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserBaseDao userBaseDao;

    @Override
    public UserLoginToken getUserLoginToken(LoginEnvironment loginEnvironment, UserLoginType userLoginType, String loginName) {
        return null;
    }

    @Override
    public UserToken login(String token, String validateCode, String passWord) {
        return null;
    }

    @Override
    public TokenState logout(String token) {
        return null;
    }


    @Override
    public UserRegisterToken getUserRegisterToken(LoginEnvironment loginEnvironment, UserLoginType userLoginType, String loginName) {
        if (userLoginType == UserLoginType.Phone) {
            return getPhoneRegisterToken(loginEnvironment, loginName);
        } else if (userLoginType == UserLoginType.UserName) {
            return getUserNameRegisterToken(loginEnvironment, loginName);
        }
        //不支持的注册方式
        return new UserRegisterToken(TokenState.NotSupportType);
    }

    @Override
    public TokenState register(String token, String code, String passWord) {
        return null;
    }

    /**
     * 获取收注册令牌
     *
     * @param loginEnvironment
     * @param loginName
     * @return
     */
    public UserRegisterToken getPhoneRegisterToken(LoginEnvironment loginEnvironment, String loginName) {
        if (this.userBaseDao.existsByPhone(loginName)) {
            return new UserRegisterToken(TokenState.NotSupportType);
        }
        return null;

    }


    /**
     * 获取用户名注册令牌
     *
     * @param loginEnvironment
     * @param loginName
     * @return
     */
    public UserRegisterToken getUserNameRegisterToken(LoginEnvironment loginEnvironment, String loginName) {
        if (this.userBaseDao.existsByUserName(loginName)) {
            return new UserRegisterToken(TokenState.NotSupportType);
        }
        return null;
    }


}
