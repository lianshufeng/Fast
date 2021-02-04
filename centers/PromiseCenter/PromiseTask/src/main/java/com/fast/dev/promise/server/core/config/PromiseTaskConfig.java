package com.fast.dev.promise.server.core.config;

import com.fast.dev.promise.config.PromiseClientConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "promisetask")
@Import(PromiseClientConfiguration.class)
public class PromiseTaskConfig {


    /**
     * 并发执行的任务数
     */
    private int maxExecuteTaskCount = 5;



}
