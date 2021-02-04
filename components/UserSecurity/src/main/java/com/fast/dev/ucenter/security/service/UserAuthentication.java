package com.fast.dev.ucenter.security.service;

import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.security.model.UserIdentity;

import java.util.Set;

/**
 * 用户认证的接口
 */
public interface UserAuthentication {


    /**
     * 成功返回该对象的角色列表
     *
     * @param userTokenModel
     * @return
     */
    public UserIdentity authentication(UserTokenModel userTokenModel);


}
