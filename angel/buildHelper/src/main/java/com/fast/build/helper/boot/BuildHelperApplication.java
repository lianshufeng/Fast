package com.fast.build.helper.boot;

import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


@ComponentScan("com.fast.build.helper.core")
@Import(MVCConfiguration.class)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BuildHelperApplication extends ApplicationBootSuper {


    public static void main(String[] args) {
        SpringApplication.run(BuildHelperApplication.class, args);
    }

}
