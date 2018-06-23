package com.fast.dev.ucenter.service;

import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.acenter.annotation.EnableApplicationMonitorClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;



@ComponentScan("com.fast.dev.ucenter")

//应用中心
@EnableApplicationClient
//监控中心
@EnableApplicationMonitorClient
public class UserserverApplication extends ApplicationBootSuper {


    public static void main(String[] args) {
        SpringApplication.run(UserserverApplication.class, args);
    }



}
