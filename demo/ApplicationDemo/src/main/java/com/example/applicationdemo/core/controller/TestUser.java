package com.example.applicationdemo.core.controller;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.security.helper.UserHelper;
import com.fast.dev.ucenter.security.service.remote.RemoteUserCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者：练书锋
 * 时间：2018/8/24
 */
@RestController
public class TestUser {


    @Autowired
    private RemoteUserCenterService remoteUserCenterService;


    @Autowired
    private UserHelper userHelper;

    @RequestMapping("ping")
//    @PreAuthorize("hasRole('AUTH_USER')")
//    @Secured({"test","AUTH_USER"})
    @Secured({"user"})
    public Object ping() {
        System.out.println("user:" + JsonUtil.toJson(userHelper.getUser()));
        return InvokerResult.success("ping");
    }


    @RequestMapping("test")
    public Object test() {
        System.out.println("user:" + JsonUtil.toJson(userHelper.getUser()));
        return InvokerResult.success("login");
    }


    @RequestMapping("excpiton")
    public Object excpiton(String info) {
        Assert.hasText(info,"不能为空");
        System.out.println("user:" + JsonUtil.toJson(userHelper.getUser()));
        return InvokerResult.success("login");
    }

    @RequestMapping("logout")
    @Secured({"user"})
    public Object logout() {
        System.out.println("user:" + JsonUtil.toJson(userHelper.getUser()));
        //调用管理模块进行通知注销
        this.remoteUserCenterService.logout(userHelper.getUser().getuToken());
        return InvokerResult.success("logout");
    }


}
