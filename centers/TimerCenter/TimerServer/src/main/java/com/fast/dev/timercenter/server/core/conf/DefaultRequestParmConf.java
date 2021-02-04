package com.fast.dev.timercenter.server.core.conf;

import com.fast.dev.timercenter.service.model.RequestParmModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "timer.task.default")
public class DefaultRequestParmConf extends RequestParmModel {

    /**
     * 执行延迟时间，单位秒
     */
    private Long delayTime = 0l;



}
