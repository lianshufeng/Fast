package com.fast.dev.demo.core.config;

import com.fast.dev.promise.config.PromiseClientConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(PromiseClientConfiguration.class)
public class PromiseClientConfig {
}
