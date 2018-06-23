package com.fast.dev.core.boot;

import com.fast.dev.core.mvc.MVCConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(MVCConfiguration.class)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
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
