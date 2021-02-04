package com.fast.dev.timercenter.server.core.conf;

import com.fast.dev.timercenter.service.config.TimerCenterClientConfiguration;
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
@ConfigurationProperties(prefix = "timer.task.process")
@Import(TimerCenterClientConfiguration.class)
public class TimerCenterServerConf {


    /**
     * 并发执行的任务数
     */
    private int maxExecuteTaskCount = 30;



}
