package com.fast.dev.ucenter.security.service.remote;

import com.fast.dev.ucenter.core.model.BaseUserModel;
import com.fast.dev.ucenter.core.model.TokenEnvironment;
import com.fast.dev.ucenter.core.model.UserRegisterModel;
import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.core.type.PassWordEncodeType;
import com.fast.dev.ucenter.core.type.TokenState;
import com.fast.dev.ucenter.core.type.UserLoginType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 断路器，异常的情况处理
 */
public class RemoteUserCenterServiceError implements RemoteUserCenterService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteUserCenterService.class);

    @Override
    public UserTokenModel queryByUserToken(String token) {
        logger.error("query token : [" + token + "]  error");
        return null;
    }

    @Override
    public boolean logout(String token) {
        logger.error("logout token : [" + token + "]  error");
        return false;
    }

    @Override
    public BaseUserModel queryUserId(String uid) {
        logger.error("queryUserId uid : [" + uid + "]  error");
        return null;
    }

    @Override
    public BaseUserModel queryByLoginName(UserLoginType loginType, String loginName) {
        logger.error("queryByLoginName loginName : [" + loginName + "]  error");
        return null;
    }

    @Override
    public UserRegisterModel addUser(UserLoginType loginType, String loginName, String passWord) {
        return null;
    }

    @Override
    public UserTokenModel login(UserLoginType loginType, String loginName, String password, Long expireTime, TokenEnvironment env) {
        return null;
    }


    @Override
    public UserTokenModel createToken(String uid, Long expireTime, TokenEnvironment env) {
        return null;
    }

    @Override
    public UserRegisterModel insertBaseUser(UserLoginType loginType, String loginName, String salt, String passWord, PassWordEncodeType encodeType) {
        return null;
    }

    @Override
    public BaseUserModel updateLoginValue(String uid, UserLoginType loginType, String loginName) {
        return null;
    }

    @Override
    public TokenState setUserPassWord(String uid, String passWord) {
        return null;
    }

    @Override
    public long cleanUserToken(String uid, String[] ignoreUToken) {
        return 0;
    }
}
