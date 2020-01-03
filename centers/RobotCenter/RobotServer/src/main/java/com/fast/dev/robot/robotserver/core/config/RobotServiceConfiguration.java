package com.fast.dev.robot.robotserver.core.config;

import com.fast.dev.robot.service.config.RobotServiceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(RobotServiceConfig.class)
public class RobotServiceConfiguration {
}
