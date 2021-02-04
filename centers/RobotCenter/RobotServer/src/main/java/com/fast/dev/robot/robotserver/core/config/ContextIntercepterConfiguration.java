package com.fast.dev.robot.robotserver.core.config;

import com.fast.dev.core.filter.ContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContextIntercepterConfiguration {


    @Bean
    public ContextFilter contextFilter() {
        return new ContextFilter();
    }


}
