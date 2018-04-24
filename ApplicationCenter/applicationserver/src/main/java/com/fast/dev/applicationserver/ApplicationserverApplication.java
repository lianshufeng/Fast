package com.fast.dev.applicationserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ApplicationserverApplication extends SpringBootServletInitializer {

    /**
     * 兼容Web容器启动
     * @param application
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApplicationserverApplication.class);
    }


    public static void main(String[] args) {
        SpringApplication.run(ApplicationserverApplication.class, args);
    }
}
