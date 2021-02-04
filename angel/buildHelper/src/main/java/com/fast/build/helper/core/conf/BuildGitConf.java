package com.fast.build.helper.core.conf;

import com.fast.build.helper.core.type.GitType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "build.git")
public class BuildGitConf {


    /**
     * 地址
     */
    private String host;


    /**
     * 帐号
     */
    private String userName;


    /**
     * 密码
     */
    private String passWord;


    /**
     * 功能令牌
     */
    private String token;

    /**
     * git 类型
     */
    private GitType type;


}
