package com.fast.dev.ucenter.core.controller.manager;

import com.fast.dev.ucenter.core.model.TokenEnvironment;
import com.fast.dev.ucenter.core.service.UserManagerService;
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
     * @param loginType
     * @param loginName
     * @param passWord
     * @return
     */
    @RequestMapping("addUser")
    public Object addUser(UserLoginType loginType,String loginName,String passWord) {
        Assert.notNull(loginType, "类型不能为空");
        Assert.hasText(loginName, "登陆账号不能为空");
        Assert.hasText(passWord, "密码不能为空");
        return this.userManager.addUser(loginType,loginName,passWord);
    }

    /**
     * 登录用户
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
}
