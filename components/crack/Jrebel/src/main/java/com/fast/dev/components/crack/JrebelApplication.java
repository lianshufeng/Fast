package com.fast.dev.components.crack;

import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


@ComponentScan("com.fast.dev.components.crack")
@Import(MVCConfiguration.class)
public class JrebelApplication extends ApplicationBootSuper {


    public static void main(String[] args) {
        SpringApplication.run(JrebelApplication.class, args);
    }



}
