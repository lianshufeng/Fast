package com.fast.build.helper.core.conf;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "build.maven")
public class MavenConf {

    /**
     * 配置maven路径
     */
    private String home = "/opt/maven/";
}
