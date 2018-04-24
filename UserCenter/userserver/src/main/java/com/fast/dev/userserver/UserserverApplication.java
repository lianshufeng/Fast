package com.fast.dev.userserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@EnableEurekaClient
@SpringBootApplication
@ComponentScan("com.fast.dev.user")
@Import(MVCConfiguration.class)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class UserserverApplication extends SpringBootServletInitializer {


    /**
     * 兼容Web容器启动
     *
     * @param application
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(UserserverApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(UserserverApplication.class, args);
    }
}
