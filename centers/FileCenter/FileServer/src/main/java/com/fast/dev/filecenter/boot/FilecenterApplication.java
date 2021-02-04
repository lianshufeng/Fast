package com.fast.dev.filecenter.boot;

import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * 文件中心启动
 */
@EnableApplicationClient
@Import(MVCConfiguration.class)
@ComponentScan("com.fast.dev.filecenter.core")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class FilecenterApplication extends ApplicationBootSuper {

    public static void main(String[] args) {
        SpringApplication.run(FilecenterApplication.class, args);
    }
}
