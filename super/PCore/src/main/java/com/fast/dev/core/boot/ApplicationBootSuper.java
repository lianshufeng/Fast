package com.fast.dev.core.boot;

import com.fast.dev.core.runner.BannerApplicationRunner;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//加载默认的端点
@ComponentScan("com.fast.dev.core.endpoints")
//加载全局配置
@ComponentScan("com.fast.dev.core.conf")
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@Configuration
public abstract class ApplicationBootSuper extends SpringBootServletInitializer {


    /**
     * 启动代码
     *
     * @param cls
     * @param args
     */
    public static void run(Class<?> cls, String[] args) {
        SpringApplication springApplication = new SpringApplication();
        springApplication.run(cls, args);
    }


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



    /**
     * 启动成功后打印 Banner
     *
     * @return
     */
    @Bean
    public ApplicationRunner BannerApplicationRunner() {
        return new BannerApplicationRunner();
    }


}
