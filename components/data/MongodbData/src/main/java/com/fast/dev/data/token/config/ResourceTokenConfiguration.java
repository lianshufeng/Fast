package com.fast.dev.data.token.config;

import com.fast.dev.data.token.service.ResourceTokenService;
import com.fast.dev.data.token.service.impl.ResourceTokenServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {"com.fast.dev.data.token.dao"})
public class ResourceTokenConfiguration {

    @Bean
    public ResourceTokenService resourceTokenService() {
        return new ResourceTokenServiceImpl();
    }

}
