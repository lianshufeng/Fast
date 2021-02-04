package com.fast.dev.auth.config;

import com.fast.dev.ucenter.security.config.UCenterSecurity;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@ComponentScan("com.fast.dev.auth.client")
@ComponentScan("com.fast.dev.auth.security")
@EnableFeignClients("com.fast.dev.auth.client.service")
@Import({UCenterSecurity.class})
@Configuration
public class AuthClientConfig {


}
