package com.example.applicationdemo;

import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import org.springframework.boot.SpringApplication;

@EnableApplicationClient
public class ApplicationdemoApplication extends ApplicationBootSuper {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationdemoApplication.class, args);
    }
}
