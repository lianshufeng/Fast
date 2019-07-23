package com.example.boot;

import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan("com.example.core")
@Import(MVCConfiguration.class)
@EnableApplicationClient
public class DemoApplication extends ApplicationBootSuper {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
