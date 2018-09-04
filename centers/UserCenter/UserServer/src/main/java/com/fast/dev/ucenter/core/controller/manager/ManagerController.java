package com.fast.dev.ucenter.core.controller.manager;

import com.fast.dev.ucenter.core.service.UserManagerService;
import com.fast.dev.ucenter.core.type.UserLoginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
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


}
