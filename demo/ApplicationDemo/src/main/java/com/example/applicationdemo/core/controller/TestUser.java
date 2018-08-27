package com.example.applicationdemo.core.controller;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.ucenter.security.helper.UserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者：练书锋
 * 时间：2018/8/24
 */
@RestController
public class TestUser {


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


    @RequestMapping("login")
    public Object login() {
        System.out.println("user:" + JsonUtil.toJson(userHelper.getUser()));
        return InvokerResult.success("login");
    }


}
