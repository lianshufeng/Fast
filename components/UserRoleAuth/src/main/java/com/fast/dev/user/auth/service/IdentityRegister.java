package com.fast.dev.user.auth.service;

import com.fast.dev.user.auth.model.IdentityModel;

import java.util.Set;

/**
 * 身份注册接口
 */
public interface IdentityRegister {


    /**
     * 注册身份列表
     *
     * @return
     */
    void register(Set<IdentityModel> identityModels);


}
