package com.fast.dev.ucenter.boot;

import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


//应用中心

@ComponentScan("com.fast.dev.ucenter")
@Import(MVCConfiguration.class)
@EnableApplicationClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class UserserverApplication extends ApplicationBootSuper {

    public static void main(String[] args) {
        SpringApplication.run(UserserverApplication.class, args);
    }


}
