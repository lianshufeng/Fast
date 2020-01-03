package com.fast.dev.user.auth.config;

import com.fast.dev.data.mongo.config.MongoConfiguration;
import com.fast.dev.ucenter.security.config.UCenterSecurity;
import com.fast.dev.user.auth.service.IdentityRegister;
import com.fast.dev.user.auth.service.UserService;
import com.fast.dev.user.auth.service.impl.DefaultIdentityRegisterImpl;
import com.fast.dev.user.auth.service.impl.DefaultUserRoleAuthServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@Import({UCenterSecurity.class, MongoConfiguration.class})
@ComponentScan("com.fast.dev.user.auth")
@EnableMongoRepositories("com.fast.dev.user.auth.dao")
public class UserRoleAuthConfig {


    /**
     * 用户注册接口的实现
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public UserService defaultUserRoleAuthServiceImpl() {
        return new DefaultUserRoleAuthServiceImpl();
    }


    /**
     * 身份注册器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public IdentityRegister defaultIdentityRegisterImpl() {
        return new DefaultIdentityRegisterImpl();
    }


}
