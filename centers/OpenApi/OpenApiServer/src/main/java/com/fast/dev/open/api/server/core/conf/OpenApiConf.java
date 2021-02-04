package com.fast.dev.open.api.server.core.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "openapi")
public class OpenApiConf {

    //时间最大的有效期
    private long maxTimeInvalid = 1000 * 60 * 10;


}
