package com.fast.dev.pay.server.core.config;

import com.fast.dev.pay.config.PayServerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(PayServerConfig.class)
public class PayServerConfiguration {
}
