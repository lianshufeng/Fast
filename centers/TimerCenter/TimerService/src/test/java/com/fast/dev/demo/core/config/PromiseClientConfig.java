package com.fast.dev.demo.core.config;

import com.fast.dev.timercenter.service.config.TimerCenterClientConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(TimerCenterClientConfiguration.class)
public class PromiseClientConfig {
}
