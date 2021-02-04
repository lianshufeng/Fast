package com.example.applicationdemo.core.controller;

import com.example.applicationdemo.core.service.TestService;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.log.helper.UserLogHelper;
import com.fast.dev.auth.client.model.EnterpriseModel;
import com.fast.dev.auth.client.model.ResultContent;
import com.fast.dev.auth.client.model.RoleModel;
import com.fast.dev.auth.client.model.UserModel;
import com.fast.dev.auth.client.service.EnterpriseService;
import com.fast.dev.auth.client.service.RoleService;
import com.fast.dev.auth.client.service.UserService;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import com.fast.dev.ucenter.security.service.remote.RemoteUserCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者：练书锋
 * 时间：2018/8/24
 */
@Slf4j
// 支持动态刷新配置
@RefreshScope

@RestController
public class TestUser {


    @Autowired
    private RemoteUserCenterService remoteUserCenterService;

    @Autowired
    private UserLogHelper userLogHelper;

    @Autowired
    private TestService testService;


    @Autowired
    private EnterpriseService enterpriseService;


    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;


    @RequestMapping("updateUser")
    public Object updateUser(String epId, UserModel userModel) {
        return this.userService.updateUser(epId, userModel);
    }


    @RequestMapping("listEnterprise")
    public Object test(EnterpriseModel enterpriseModel, @PageableDefault Pageable pageable) {
        return enterpriseService.list(enterpriseModel, pageable);
    }


    @RequestMapping("test")
    @ResourceAuth(value = "test", remark = "测试的权限")
    @UserLog(action = "test_action", parameter = {"#name"})
    public ResultContent test(String name) {
        System.out.println(name);

        this.userLogHelper.log("name", name + "_");
        this.userLogHelper.log("name1", name + "_1");
        this.userLogHelper.log("name2", name + "_2");


        testService.test(name);


        return ResultContent.buildContent(name);
    }


    @RequestMapping("log")
    @UserLog()
    public Object log(String name) {
        return ResultContent.buildContent(name);
    }


}
