package com.example.applicationdemo.core.service;

import com.example.applicationdemo.core.model.UserModel;
import com.fast.dev.auth.client.log.annotations.UserLog;
import com.fast.dev.auth.client.log.helper.UserLogHelper;
import com.fast.dev.auth.client.service.RoleService;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {

//
//    @Autowired
//    private RoleService roleService;



    @ResourceAuth(value = "queryList", remark = "查询集合")
    public void queryList(UserModel userModel, String[] auths) {

    }

    @Autowired
    private UserLogHelper userLogHelper;

    @UserLog(parameter = "#xx")
    public void test(String xx) {
        userLogHelper.log("xx", xx+"_test");
    }

}
