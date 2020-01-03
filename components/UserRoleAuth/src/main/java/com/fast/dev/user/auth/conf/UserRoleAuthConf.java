package com.fast.dev.user.auth.conf;

import com.fast.dev.user.auth.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "user.auth")
public class UserRoleAuthConf {

    //用户实体的class
    private Class<? extends User> userEntityCls;

    //用户模型的class
    private Class userModelCls;

    //默认的登录权限
    private String defaultLoginAuth = "user";

}
