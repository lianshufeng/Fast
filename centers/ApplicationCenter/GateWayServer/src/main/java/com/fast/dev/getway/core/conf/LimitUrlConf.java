package com.fast.dev.getway.core.conf;

import com.fast.dev.getway.core.model.LimitRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "limit")
public class LimitUrlConf {


    /**
     * 规则
     */
    private LinkedHashMap<String, LimitRole> roles;


}
