package com.fast.dev.ucenter.security.config;

import com.fast.dev.core.mvc.MVCConfiguration;
import com.fast.dev.ucenter.security.config.impl.MethodSecurityConfig;
import com.fast.dev.ucenter.security.config.impl.UserSecurityBeansConfig;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


/**
 * 作者：练书锋
 * 时间：2018/8/24RoleVoterConfig
 */


@Order
@EnableWebSecurity
@EnableFeignClients("com.fast.dev.ucenter.core.batch")
@Import({MemoryCacheConfig.class, MVCConfiguration.class, UserSecurityBeansConfig.class, MethodSecurityConfig.class})
public class UCenterSecurity extends WebSecurityConfigurerAdapter {


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
                .logout()
                .disable()
                .authorizeRequests()
                .anyRequest()
                .permitAll();
    }


}



