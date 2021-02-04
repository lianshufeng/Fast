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
@ConfigurationProperties(prefix = "build.task")
public class BuildTaskConf {


    /**
     * 核心项目, lianshufeng/BaseFast
     */
    private String coreProject;


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
     * 应用资源路径
     */
    private String appResourceName = "app";


    /**
     * 资源的备份名
     */
    private String backupResourceName = "backup";


    /**
     * 最大更新git项目线程数量
     */
    private int maxPullGitThreadCount = 5;


    /**
     * 最大执行任务数
     */
    private int maxExecuteTaskCount = 5;


}
