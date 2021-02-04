package com.fast.dev.timercenter.server.core.config;

import com.fast.dev.core.mvc.MVCConfiguration;
import com.fast.dev.core.mvc.MVCResponseConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MVCResponseConfiguration.class, MVCConfiguration.class})
public class MVCConfig {
}
