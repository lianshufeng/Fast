package com.fast.dev.ucenter.core.config;

import com.fast.dev.ucenter.core.helper.UserCenterOuputStreamHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserCenterStreamConfig {

    @Bean
    public UserCenterOuputStreamHelper userCenterOuputStreamHelper() {
        return new UserCenterOuputStreamHelper();
    }


}
