package com.example.applicationdemo.core.config;

import com.fast.dev.core.mvc.MVCConfiguration;
import com.fast.dev.ucenter.security.config.UCenterSecurity;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 作者：练书锋
 * 时间：2018/8/24
 * 配置使用用户中心的客户端
 */
@Configuration
@Import({UCenterSecurity.class})
public class ConfigUserSecurity {
}
