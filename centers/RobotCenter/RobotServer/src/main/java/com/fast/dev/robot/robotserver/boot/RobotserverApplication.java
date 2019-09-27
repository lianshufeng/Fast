package com.fast.dev.robot.robotserver.boot;

import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan("com.fast.dev.robot.robotserver.core")
@Import(MVCConfiguration.class)
@EnableApplicationClient
public class RobotserverApplication extends ApplicationBootSuper {

    public static void main(String[] args) {
        SpringApplication.run(RobotserverApplication.class, args);
    }

}
