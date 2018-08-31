package com.fast.dev.ucenter.core.dao.extend;

import com.fast.dev.ucenter.core.domain.BaseUser;
import com.fast.dev.ucenter.core.type.UserLoginType;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 */
public interface BaseUserDaoExtend {


    /**
     * 查询用户的salt值
     *
     * @param userLoginType
     * @param loginName
     * @return
     */
    public String queryUserSalt(UserLoginType userLoginType, String loginName);


    /**
     * 查询或者保存
     *
     * @param phone
     * @return
     */
    public BaseUser findAndSaveBaseUser(String phone);

}
