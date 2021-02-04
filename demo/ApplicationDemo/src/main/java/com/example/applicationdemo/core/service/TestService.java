package com.example.applicationdemo.core.service;

import com.example.applicationdemo.core.model.UserModel;
import com.fast.dev.ucenter.security.resauth.annotations.ResourceAuth;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    //    @PreAuthorize("hasAnyAuthority(#auths) ")
//    @PreAuthorize("hasAnyAuthority('s1_user','s2_teacher','s3_admin') ")

    @ResourceAuth(value = "queryList",remark = "查询集合")
    public void queryList(UserModel userModel, String[] auths) {

    }

}
