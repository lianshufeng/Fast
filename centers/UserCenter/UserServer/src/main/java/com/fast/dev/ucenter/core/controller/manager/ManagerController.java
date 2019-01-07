package com.fast.dev.ucenter.core.controller.manager;

import com.fast.dev.ucenter.core.model.TokenEnvironment;
import com.fast.dev.ucenter.core.service.UserManagerService;
import com.fast.dev.ucenter.core.type.PassWordEncodeType;
import com.fast.dev.ucenter.core.type.UserLoginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("manager")
public class ManagerController {

    @Autowired
    private UserManagerService userManager;

    @RequestMapping("queryUserToken")
    public Object queryUserToken(String token) {
        Assert.hasText(token, "用户令牌不能为空");
        return this.userManager.queryByUserToken(token);
    }


    @RequestMapping("logout")
    public Object logout(String token) {
        Assert.hasText(token, "用户令牌不能为空");
        return this.userManager.logout(token);
    }


    @RequestMapping("queryUserId")
    public Object queryUserId(String uid) {
        Assert.hasText(uid, "用户id不能为空");
        return this.userManager.queryUserId(uid);
    }


    @RequestMapping("queryByLoginName")
    public Object queryByLoginName(UserLoginType loginType, String loginName) {
        Assert.notNull(loginType, "登陆方式不能为空");
        Assert.hasText(loginName, "用户名不能为空");
        return this.userManager.queryByLoginName(loginType, loginName);
    }

    /**
     * 添加用户
     *
     * @param loginType
     * @param loginName
     * @param passWord
     * @return
     */
    @RequestMapping("addUser")
    public Object addUser(UserLoginType loginType, String loginName, String passWord) {
        Assert.notNull(loginType, "类型不能为空");
        Assert.hasText(loginName, "登陆账号不能为空");
        Assert.hasText(passWord, "密码不能为空");
        return this.userManager.addUser(loginType, loginName, passWord);
    }

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
    @RequestMapping("login")
    public Object login(UserLoginType loginType, String loginName, String passWord, @RequestParam(defaultValue = "604800000") Long expireTime, TokenEnvironment env) {
        Assert.notNull(loginType, "登陆方式不能为空");
        Assert.hasText(loginName, "用户名不能为空");
        Assert.hasText(passWord, "密码不能为空");
        return this.userManager.login(loginType, loginName, passWord, expireTime, env);
    }


    /**
     * 新增用户同步接口， 存在返回失败
     *
     * @param loginType
     * @param loginName
     * @param salt
     * @param passWord
     * @return
     */
    @RequestMapping("insertBaseUser")
    public Object insertBaseUser(UserLoginType loginType, String loginName, String salt, String passWord, PassWordEncodeType encodeType) {
        Assert.notNull(loginType, "loginType 不能为空");
        Assert.hasText(loginName, "loginName 不能为空");
        Assert.hasText(salt, "salt 不能为空");
        Assert.hasText(passWord, "passWord 不能为空");
        Assert.notNull(encodeType, "encodeType 编码类型不能为空");
        return this.userManager.insertBaseUser(loginType, loginName, salt, passWord, encodeType);
    }


    /**
     * 创建用户令牌
     *
     * @param uid
     * @return
     */
    @RequestMapping("createToken")
    public Object createToken(String uid, @RequestParam(defaultValue = "604800000") Long expireTime, TokenEnvironment env) {
        Assert.hasText(uid, "用户id不能为空");
        return this.userManager.createToken(uid, expireTime, env);
    }


    /**
     * 修改用户登陆方式
     *
     * @param uid
     * @return
     */
    @RequestMapping("updateLoginName")
    public Object updateLoginName(String uid, UserLoginType loginType, String loginName) {
        Assert.hasText(uid, "用户id不能为空");
        Assert.notNull(loginType, "登陆类型不能为空");
        Assert.notNull(loginName, "登陆名不能为空");
        return this.userManager.updateLoginValue(uid, loginType, loginName);
    }


    /**
     * 设置用户的密码
     *
     * @param uid
     * @return
     */
    @RequestMapping("setUserPassWord")
    public Object setUserPassWord(String uid, String passWord) {
        Assert.hasText(uid, "用户id不能为空");
        Assert.hasText(passWord, "用户密码不能为空");
        return this.userManager.setUserPassWord(uid, passWord);
    }


    /**
     * 清空用户令牌
     *
     * @param uid
     * @return
     */
    @RequestMapping("cleanUserToken")
    public Object cleanUserToken(String uid, String[] ignoreUToken) {
        Assert.hasText(uid, "用户id不能为空");
        return this.userManager.cleanUserToken(uid, ignoreUToken);
    }


}
