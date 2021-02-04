package com.fast.dev.open.api.server.core.config;

import com.fast.dev.auth.config.AuthClientConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import(AuthClientConfig.class)
public class AuthCenterConfig {
}
