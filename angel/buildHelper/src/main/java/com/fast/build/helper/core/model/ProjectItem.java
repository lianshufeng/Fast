package com.fast.build.helper.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ProjectItem {

    //项目名
    private String name;

    //仓库信息
    private GitItem git;

    //容器名
    private String[] containerName;

    //分支名
    private String branch;

    //是否需要编译
    private Boolean build;


}
