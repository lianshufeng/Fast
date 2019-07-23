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
@ConfigurationProperties(prefix = "build.git.default")
public class DefaultGitConf {


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


    /**
     * 核心项目, lianshufeng/BaseFast
     */
    private String coreProject;


    /**
     * 项目临时目录
     */
    private String tempPath = "temp";


    /**
     * 项目路径
     */
    private String projectPath = "project";

    /**
     * 编译目录
     */
    private String buildPath = "build";

    /**
     * 应用名
     */
    private String applicationPath = "application";

    /**
     * 最大更新git项目线程数量
     */
    private int maxPullGitThreadCount = 5;


}
