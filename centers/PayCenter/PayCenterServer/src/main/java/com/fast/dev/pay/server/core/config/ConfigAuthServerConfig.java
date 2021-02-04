package com.fast.dev.pay.server.core.config;

import com.fast.dev.auth.config.AuthClientConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 作者：练书锋
 * 时间：2018/8/24
 * 配置使用用户中心的客户端
 */
@Configuration
@Import({AuthClientConfig.class})
public class ConfigAuthServerConfig {
}
