package com.fast.dev.open.api.server.core.config;


import com.fast.dev.core.mvc.MVCConfiguration;
import com.fast.dev.core.mvc.MVCResponseConfiguration;
import com.fast.dev.core.util.result.InvokerExceptionResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MVCConfiguration.class})
public class MVCConfig {
    /**
     * 通用的异常捕获
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public InvokerExceptionResolver invokerExceptionResolver() {
        return new InvokerExceptionResolver();
    }
}
