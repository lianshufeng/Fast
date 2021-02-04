package com.fast.build.helper.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应用GIT的配置信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// null 不序列化
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationGitInfo extends GitInfo {


    /**
     * 使用的分支
     */
    private String branch="master";


    /**
     * 是否需要编译
     */
    private boolean build;



}
