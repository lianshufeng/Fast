package com.fast.build.helper.boot;

import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@ComponentScan("com.fast.build.helper.core")
@Import(MVCConfiguration.class)
public class BuildHelperApplication extends ApplicationBootSuper {


    public static void main(String[] args) {
        SpringApplication.run(BuildHelperApplication.class, args);
    }

}
