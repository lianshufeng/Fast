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
import com.fast.dev.ucenter.security.stream.UserStream;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * 注册所有需要依赖的bean
 */


@ComponentScan({"com.fast.dev.ucenter.security.stream"})
public class UserSecurityBeansConfig {



    @Bean
    @ConditionalOnMissingBean
    public UserStream userStream(){
        return new UserStream();
    }


    @Bean
    @ConditionalOnMissingBean
    public UserTokenInterceptor userTokenInterceptor() {
        return new UserTokenInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityAuthenticationHelper securityAuthenticationHelper() {
        return new SecurityAuthenticationHelper();
    }

    @Bean
    @ConditionalOnMissingBean
    public UserTokenCache userTokenCache() {
        return new UserTokenCache();
    }


    @Bean
    @ConditionalOnMissingBean
    public UserCenterService userCenterService() {
        return new UserCenterService();
    }

    @Bean
    @ConditionalOnMissingBean
    public UserSecurityConf userSecurityConf() {
        return new UserSecurityConf();
    }

    @Bean
    @ConditionalOnMissingBean
    public UserHelper userHelper() {
        return new UserHelper();
    }


    @Bean
    @ConditionalOnMissingBean
    public ResourcesAuthHelper resourcesAuthHelper() {
        return new ResourcesAuthHelper();
    }


    @Bean
    @ConditionalOnMissingBean
    public RemoteUserCenterService remoteUserCenterService() {
        return new RemoteUserCenterServiceImpl();
    }


}
