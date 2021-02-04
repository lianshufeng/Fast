package com.fast.dev.ucenter.core.config;


import com.fast.dev.ucenter.core.batch.BatchUserService;
import com.fast.dev.ucenter.core.service.impl.BatchUserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchUserServiceConfig {


    /**
     * 服务端启动注册为本地的服务
     *
     * @return
     */
    @Bean
    public BatchUserService batchUserService() {
        return new BatchUserServiceImpl();
    }


}
