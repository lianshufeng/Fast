package com.fast.dev.open.api.server.core.config;

import com.fast.dev.openapi.config.OpenApiServerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(OpenApiServerConfig.class)
public class OpenApiConfig {
}
