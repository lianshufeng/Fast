package com.fast.dev.ucenter.security.conf;

import com.fast.dev.core.mvc.MVCConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "usersecurity")
public class UserSecurityConf {

    // 角色的前缀名
    private String rolePrefixName = "ROLE_";

    // 需要拦截方法的URL
    private String[] needSecurityMethodUrl = new String[]{"/**/*"};

    // 在拦截的URL中排除不拦截的URL
    private String[] excludeSecurityMethodUrl = new String[]{"/" + MVCConfiguration.StaticResources + "/**", "/error"};

}
