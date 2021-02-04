package com.fast.build.helper.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 项目任务
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTask {

    //项目名,如果为空则取所有配置项目，且build=true的项目
    private String[] projectName;

    //Maven项目的包名：com.fast.dev.PayCenterServer
    private String[] artifact;

    //是否编译
    private boolean build;

    //是否重启
    private boolean restart;


}
