package com.fast.dev.auth.center.server.core.config;


import com.fast.dev.core.mvc.MVCConfiguration;
import com.fast.dev.core.mvc.MVCResponseConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({MVCConfiguration.class, MVCResponseConfiguration.class})
public class MVCConfig {
}
