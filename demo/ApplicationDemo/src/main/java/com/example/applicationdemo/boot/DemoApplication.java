package com.example.applicationdemo.boot;


import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@EnableApplicationClient
@ComponentScan("com.example.applicationdemo.core")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DemoApplication extends ApplicationBootSuper {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

