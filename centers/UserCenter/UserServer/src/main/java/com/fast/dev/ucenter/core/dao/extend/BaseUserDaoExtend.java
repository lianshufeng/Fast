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


    /**
     * 查询用户详情
     *
     * @param loginType
     * @param loginName
     * @return
     */
    public BaseUser queryByLoginName(UserLoginType loginType, String loginName);


    /**
     * 设置用户密码
     *
     * @param passWord
     * @return
     */
    public boolean updatePassWord(String id, String salt, String passWord);


    /**
     * 通过登陆方式查询该用户是否已存在
     * @param loginType
     * @param loginName
     * @return
     */
    boolean existsByLoginName(UserLoginType loginType, String loginName);


    /**
     * 修改用户的登陆方式
     * @param uid
     * @param loginType
     * @param loginName
     * @return
     */
    BaseUser updateLoginValue(String uid , UserLoginType loginType, String loginName);

}
