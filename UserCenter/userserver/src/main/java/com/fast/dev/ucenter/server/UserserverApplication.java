package com.fast.dev.ucenter.server;

import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;



@ComponentScan("com.fast.dev.ucenter")
@Import(MVCConfiguration.class)
public class UserserverApplication extends ApplicationBootSuper {


    public static void main(String[] args) {
        SpringApplication.run(UserserverApplication.class, args);
    }
}
