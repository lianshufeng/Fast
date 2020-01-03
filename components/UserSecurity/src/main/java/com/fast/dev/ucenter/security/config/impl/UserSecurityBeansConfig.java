package com.fast.dev.ucenter.security.config.impl;

import com.fast.dev.ucenter.security.cache.UserTokenCache;
import com.fast.dev.ucenter.security.conf.UserSecurityConf;
import com.fast.dev.ucenter.security.helper.SecurityAuthenticationHelper;
import com.fast.dev.ucenter.security.helper.UserHelper;
import com.fast.dev.ucenter.security.interceptors.UserTokenInterceptor;
import com.fast.dev.ucenter.security.resauth.ResourcesAuthHelper;
import com.fast.dev.ucenter.security.service.UserCenterService;
import com.fast.dev.ucenter.security.service.remote.RemoteUserCenterService;
import com.fast.dev.ucenter.security.service.remote.impl.RemoteUserCenterServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * 注册所有需要依赖的bean
 */


@ComponentScan({"com.fast.dev.ucenter.security.stream"})
public class UserSecurityBeansConfig {

    @Bean
    public UserTokenInterceptor userTokenInterceptor() {
        return new UserTokenInterceptor();
    }

    @Bean
    public SecurityAuthenticationHelper securityAuthenticationHelper() {
        return new SecurityAuthenticationHelper();
    }

    @Bean
    public UserTokenCache userTokenCache() {
        return new UserTokenCache();
    }


    @Bean
    public UserCenterService userCenterService() {
        return new UserCenterService();
    }

    @Bean
    public UserSecurityConf userSecurityConf() {
        return new UserSecurityConf();
    }

    @Bean
    public UserHelper userHelper() {
        return new UserHelper();
    }


    @Bean
    public ResourcesAuthHelper resourcesAuthHelper() {
        return new ResourcesAuthHelper();
    }


    @Bean
    public RemoteUserCenterService remoteUserCenterService() {
        return new RemoteUserCenterServiceImpl();
    }


}
