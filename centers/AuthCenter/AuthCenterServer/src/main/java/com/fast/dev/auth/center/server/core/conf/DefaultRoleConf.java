package com.fast.dev.auth.center.server.core.conf;

import com.fast.dev.auth.client.constant.ResourceAuthConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "auth.role")
public class DefaultRoleConf {



    /**
     * 默认的登录权限
     */
    private String defaultLoginAuth = ResourceAuthConstant.User;


    /**
     * 禁止其他角色组中出现的角色
     */
    private Set<String> authNameBlacklist = new HashSet<String>() {{
        add(ResourceAuthConstant.SuperAdmin);
        add(ResourceAuthConstant.User);
    }};
}
