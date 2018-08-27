package com.fast.dev.ucenter.security.config;

import com.fast.dev.ucenter.security.cache.UserTokenCache;
import com.fast.dev.ucenter.security.conf.UserSecurityConf;
import com.fast.dev.ucenter.security.conf.UserTokenCacheConf;
import com.fast.dev.ucenter.security.helper.SecurityAuthenticationHelper;
import com.fast.dev.ucenter.security.helper.UserHelper;
import com.fast.dev.ucenter.security.interceptors.UserTokenInterceptor;
import com.fast.dev.ucenter.security.service.UserCenterService;
import com.fast.dev.ucenter.security.service.remote.RemoteUserCenterServiceError;
import org.springframework.context.annotation.Bean;

/**
 * 注册所有需要依赖的bean
 */
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
    public UserTokenCacheConf userTokenConf() {
        return new UserTokenCacheConf();
    }

    @Bean
    public UserCenterService remoteUserCenterService() {
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
    public RemoteUserCenterServiceError remoteUserCenterServiceError() {
        return new RemoteUserCenterServiceError();
    }

}
