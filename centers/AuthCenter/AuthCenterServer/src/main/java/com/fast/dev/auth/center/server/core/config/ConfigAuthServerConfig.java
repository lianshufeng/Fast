package com.fast.dev.auth.center.server.core.config;

import com.fast.dev.auth.config.AuthServerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 作者：练书锋
 * 时间：2018/8/24
 * 配置使用用户中心的客户端
 */
@Configuration
@Import({AuthServerConfig.class})
public class ConfigAuthServerConfig {
}
