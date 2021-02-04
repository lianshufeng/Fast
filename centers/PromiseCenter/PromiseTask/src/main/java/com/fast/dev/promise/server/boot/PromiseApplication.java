package com.fast.dev.promise.server.boot;

import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


@Import(MVCConfiguration.class)
@EnableApplicationClient
@ComponentScan("com.fast.dev.promise")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PromiseApplication extends ApplicationBootSuper {

    public static void main(String[] args) {
        SpringApplication.run(PromiseApplication.class, args);
    }

}
