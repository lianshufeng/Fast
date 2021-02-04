package com.fast.dev.auth.center.server.core.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "user.log")
public class UserLogConf {


    //    time to live , 保存日志时间
    private long TTL = 1000 * 60 * 60 * 24 * 14;





}
