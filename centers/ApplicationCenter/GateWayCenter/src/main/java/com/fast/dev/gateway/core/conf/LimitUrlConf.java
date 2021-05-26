package com.fast.dev.gateway.core.conf;

import com.fast.dev.gateway.core.model.LimitRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

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
