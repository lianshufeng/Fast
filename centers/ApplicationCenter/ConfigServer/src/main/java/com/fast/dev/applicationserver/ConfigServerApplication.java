package com.fast.dev.applicationserver;

import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import org.springframework.boot.SpringApplication;


@EnableApplicationClient
public class ConfigServerApplication extends ApplicationBootSuper {


    /**
     * 默认入口方法
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

}
