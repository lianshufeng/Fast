package com.fast.dev.auth.center.server.boot;

import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@EnableApplicationClient
@ComponentScan("com.fast.dev.auth.center.server.core")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class AuthCenterServerApplication extends ApplicationBootSuper {

    public static void main(String[] args) {
        SpringApplication.run(AuthCenterServerApplication.class, args);
    }

}
