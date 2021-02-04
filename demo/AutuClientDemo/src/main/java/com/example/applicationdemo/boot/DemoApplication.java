package com.example.applicationdemo.boot;


import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import com.fast.dev.core.mvc.MVCResponseConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;


@EnableApplicationClient
@ComponentScan("com.example.applicationdemo.core")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@Import({MVCConfiguration.class, MVCResponseConfiguration.class})
public class DemoApplication extends ApplicationBootSuper {

    public static void main(String[] args) {
        run(DemoApplication.class, args);
    }
}

