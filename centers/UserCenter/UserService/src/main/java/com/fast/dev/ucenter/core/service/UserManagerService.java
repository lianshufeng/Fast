package com.fast.dev.ucenter.core.service;

import com.fast.dev.ucenter.core.model.BaseUserModel;
import com.fast.dev.ucenter.core.model.TokenEnvironment;
import com.fast.dev.ucenter.core.model.UserRegisterModel;
import com.fast.dev.ucenter.core.model.UserTokenModel;
import com.fast.dev.ucenter.core.type.PassWordEncodeType;
import com.fast.dev.ucenter.core.type.TokenState;
import com.fast.dev.ucenter.core.type.UserLoginType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 功能解决
 */

public interface UserManagerService {


    /**
     * 查询用户令牌的详情
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/ucenter/manager/queryUserToken", method = RequestMethod.POST)
    UserTokenModel queryByUserToken(@RequestParam("token") String token);


    /**
     * 注销这个用户令牌
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/ucenter/manager/logout", method = RequestMethod.POST)
    boolean logout(@RequestParam("token") String token);


    /**
     * 通过用户id查询
     *
     * @param uid
     * @return
     */
    @RequestMapping(value = "/ucenter/manager/queryUserId", method = RequestMethod.POST)
    BaseUserModel queryUserId(@RequestParam("uid") String uid);


    /**
     * 通过登陆方式查询用户
     *
     * @param loginType
     * @param loginName
     * @return
     */

    @RequestMapping(value = "/ucenter/manager/queryByLoginName", method = RequestMethod.POST)
    BaseUserModel queryByLoginName(@RequestParam("loginType") UserLoginType loginType, @RequestParam("loginName") String loginName);


    /**
     * 添加用户
     *
     * @param loginType
     * @param loginName
     * @param passWord
     * @return
     */
    @RequestMapping(value = "/ucenter/manager/addUser", method = RequestMethod.POST)
    UserRegisterModel addUser(@RequestParam("loginType") UserLoginType loginType, @RequestParam("loginName") String loginName, @RequestParam("passWord") String passWord);

    /**
     * 登录用户
     *
     * @param loginType
     * @param loginName
     * @param passWord
     * @param expireTime
     * @param env
     * @return
     */
    @RequestMapping(value = "/ucenter/manager/login", method = RequestMethod.POST)
    UserTokenModel login(@RequestParam("loginType") UserLoginType loginType, @RequestParam("loginName") String loginName, @RequestParam("passWord") String passWord, @RequestParam(defaultValue = "604800000", value = "expireTime") Long expireTime, @RequestParam("env") TokenEnvironment env);


    /**
     * 通过用户id创建用户的令牌
     *
     * @param uid
     * @return
     */
    @RequestMapping(value = "/ucenter/manager/createToken", method = RequestMethod.POST)
    UserTokenModel createToken(@RequestParam("uid") String uid, @RequestParam(defaultValue = "604800000", value = "expireTime") Long expireTime, @RequestParam("env") TokenEnvironment env);


    /**
     * 新增用户登陆接口
     *
     * @param loginType
     * @param loginName
     * @param salt
     * @param passWord
     * @return
     */
    @RequestMapping(value = "/ucenter/manager/insertBaseUser", method = RequestMethod.POST)
    UserRegisterModel insertBaseUser(@RequestParam("loginType") UserLoginType loginType, @RequestParam("loginName") String loginName, @RequestParam("salt") String salt, @RequestParam("passWord") String passWord, @RequestParam("encodeType") PassWordEncodeType encodeType);


    /**
     * 修改用户的登陆值，并放回该用户
     *
     * @param uid
     * @param loginType
     * @param loginName
     * @return
     */
    @RequestMapping(value = "/ucenter/manager/updateLoginName", method = RequestMethod.POST)
    BaseUserModel updateLoginName(@RequestParam("uid") String uid, @RequestParam("loginType") UserLoginType loginType, @RequestParam("loginName") String loginName);


    /**
     * 更新用户的密码
     *
     * @param uid
     * @param passWord
     * @return
     */
    @RequestMapping(value = "/ucenter/manager/setUserPassWord", method = RequestMethod.POST)
    TokenState setUserPassWord(@RequestParam("uid") String uid, @RequestParam("passWord") String passWord);


    /**
     * 清空指定用户id的所有token
     *
     * @param uid
     * @return
     */
    @RequestMapping(value = "/ucenter/manager/cleanUserToken", method = RequestMethod.POST)
    long cleanUserToken(@RequestParam("uid") String uid, @RequestParam("ignoreUToken") String[] ignoreUToken);

}
