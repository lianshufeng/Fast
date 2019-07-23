package com.fast.build.helper.boot;

import com.fast.build.helper.core.service.CoreService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("com.fast.build.smart.core")
public class BuildHelperApplication {

    public static void main(String[] args) {
       ApplicationContext applicationContext =  SpringApplication.run(BuildHelperApplication.class, args);

        applicationContext.getBean(CoreService.class).execute(args);

    }

}
