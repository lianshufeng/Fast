package com.fast.build.helper.core.type;

import lombok.Getter;

/**
 * 任务状态
 */
public enum TaskStatus {

    //等待
    Wait(0, "等待开始"),

    //进程
    UpdateGit(0.1f, "更新仓库"),


    MakeBuildProjects(0.2f, "构建编译项目"),

    CompileProject(0.6f, "编译项目"),

    ReStartContainer(0.7f, "重启容器"),

    CopyResources(0.8f, "拷贝资源"),

    //清理文件
    Clean(0.9f, "清理临时文件"),

    //完成
    Finish(1f, "完成"),

    ;

    TaskStatus(float process, String msg) {
        this.process = process;
        this.msg = msg;
    }

    @Getter
    private float process;


    @Getter
    private String msg;


}
