package com.fast.dev.pushcenter.boot;

import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;



//应用中心

@ComponentScan("com.fast.dev.pushcenter")
@Import(MVCConfiguration.class)
@EnableApplicationClient
public class PushServerApplication extends ApplicationBootSuper {

    public static void main(String[] args) {
        SpringApplication.run(PushServerApplication.class, args);
    }


}
