package com.fast.dev.core.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "postman")
public class PostManConf {

    @Autowired
    private void init(Environment environment) {
        if (!StringUtils.hasText(this.name)) {
            this.name = environment.getProperty("spring.application.name", "应用接口");
        }
    }

    //PostMan的项目名
    private String name;

    //主机地址
    private String hostName = "{{host}}";

    //请求头
    private Map<String, Object> header = new HashMap<>();


    //Url映射的备注
    private Map<String, String> urlMappingRemark = new HashMap<>() {{
        put("manager", "管理");
        put("endpoints", "终端");
        put("error", "错误");
    }};


}
