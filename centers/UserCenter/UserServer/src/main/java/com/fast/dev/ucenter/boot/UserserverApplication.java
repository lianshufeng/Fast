package com.fast.dev.ucenter.boot;

import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;



//应用中心
@EnableApplicationClient
@ComponentScan("com.fast.dev.ucenter")
public class UserserverApplication extends ApplicationBootSuper {

    public static void main(String[] args) {
        SpringApplication.run(UserserverApplication.class, args);
    }


}
