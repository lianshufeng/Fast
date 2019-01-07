package com.fast.dev.ucenter.core.controller.manager;

import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.core.model.TokenEnvironment;
import com.fast.dev.ucenter.core.type.PassWordEncodeType;
import com.fast.dev.ucenter.core.type.UserLoginType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("manager/rpc")
public class ManagerControllerRPC extends ManagerController {

    @Override
    public Object queryUserToken(String token) {
        return InvokerResult.notNull(super.queryUserToken(token));
    }

    @Override
    public Object logout(String token) {
        return InvokerResult.notNull(super.logout(token));
    }

    @Override
    public Object queryUserId(String uid) {
        return InvokerResult.notNull(super.queryUserId(uid));
    }

    @Override
    public Object queryByLoginName(UserLoginType loginType, String loginName) {
        return InvokerResult.notNull(super.queryByLoginName(loginType, loginName));
    }

    @Override
    public Object addUser(UserLoginType loginType, String loginName, String passWord) {
        return InvokerResult.notNull(super.addUser(loginType, loginName, passWord));
    }

    @Override
    public Object login(UserLoginType loginType, String loginName, String passWord, @RequestParam(defaultValue = "604800000") Long expireTime, TokenEnvironment env) {
        return InvokerResult.notNull(super.login(loginType, loginName, passWord, expireTime, env));
    }


    @Override
    public Object createToken(String uid, @RequestParam(defaultValue = "604800000") Long expireTime, TokenEnvironment env) {
        return InvokerResult.notNull(super.createToken(uid, expireTime, env));
    }

    @Override
    public Object insertBaseUser(UserLoginType loginType, String loginName, String salt, String passWord, PassWordEncodeType encodeType) {
        return InvokerResult.notNull(super.insertBaseUser(loginType, loginName, salt, passWord, encodeType));
    }

    @Override
    public Object updateLoginName(String uid, UserLoginType loginType, String loginName) {
        return InvokerResult.notNull(super.updateLoginName(uid, loginType, loginName));
    }


    @Override
    public Object setUserPassWord(String uid, String passWord) {
        return InvokerResult.notNull(super.setUserPassWord(uid, passWord));
    }


    @Override
    public Object cleanUserToken(String uid, String[] ignoreUToken) {
        return InvokerResult.notNull(super.cleanUserToken(uid, ignoreUToken));
    }
}
