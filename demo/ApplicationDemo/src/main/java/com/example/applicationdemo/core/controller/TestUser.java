package com.example.applicationdemo.core.controller;

import com.fast.dev.core.util.result.InvokerResult;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 作者：练书锋
 * 时间：2018/8/24
 */
@RestController
public class TestUser {

    @RequestMapping("ping")
    @PreAuthorize("hasRole('ROLE_USER')")
//    @Secured({"user", "admin"})
    public Object ping() {
        return InvokerResult.success("ping");
    }


    @RequestMapping("login")
    public Object login() {
        return InvokerResult.success("login");
    }


}
