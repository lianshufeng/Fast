package com.fast.dev.ucenter.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 作者：练书锋
 * 时间：2018/9/3
 * <p>
 * 推送中心的配置
 */


@Configuration
@ComponentScan({"com.fast.dev.pushcenter.core.stream", "com.fast.dev.pushcenter.core.helper"})
public class PushCenterConfiguration {
}
