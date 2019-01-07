package com.fast.dev.getway.boot;

import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;



@EnableZuulProxy
@EnableApplicationClient
@ComponentScan("com.fast.dev.getway.core")
public class GetWayApplication extends ApplicationBootSuper {


    /**
     * 默认入口方法
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(GetWayApplication.class, args);
    }

}
