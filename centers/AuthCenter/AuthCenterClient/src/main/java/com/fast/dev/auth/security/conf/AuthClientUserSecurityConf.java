package com.fast.dev.auth.security.conf;

import com.fast.dev.ucenter.security.conf.UserSecurityConf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class AuthClientUserSecurityConf extends UserSecurityConf {

    // 角色的前缀名
    private String rolePrefixName = "";


}
