package com.fast.dev.ucenter.security.config;

import com.fast.dev.core.mvc.MVCConfiguration;
import com.fast.dev.ucenter.security.cache.UserTokenCache;
import com.fast.dev.ucenter.security.conf.UserTokenConf;
import com.fast.dev.ucenter.security.helper.SecurityAuthenticationHelper;
import com.fast.dev.ucenter.security.interceptors.UserTokenInterceptor;
import com.fast.dev.ucenter.security.service.UserCenterService;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


/**
 * 作者：练书锋
 * 时间：2018/8/24
 */

@Import({MVCConfiguration.class})
@Order
@EnableWebSecurity
@EnableFeignClients("com.fast.dev.ucenter.security.service.remote")
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true, proxyTargetClass = true)
public class UCenterSecurity extends WebSecurityConfigurerAdapter {


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
    public UserTokenConf userTokenConf() {
        return new UserTokenConf();
    }

    @Bean
    public UserCenterService remoteUserCenterService() {
        return new UserCenterService();
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        //排除不拦截的静态资源
        web.ignoring().antMatchers("/" + MVCConfiguration.StaticResources + "/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeRequests()
                .anyRequest()
                .permitAll();
    }


}
