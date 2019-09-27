package com.fast.dev.core.boot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//加载默认的端点
@ComponentScan("com.fast.dev.core.endpoints")
//加载全局配置
@ComponentScan("com.fast.dev.core.conf")
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public abstract class ApplicationBootSuper extends SpringBootServletInitializer {


    /**
     * 兼容Web容器启动
     *
     * @param application
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(this.getClass());
    }


}
