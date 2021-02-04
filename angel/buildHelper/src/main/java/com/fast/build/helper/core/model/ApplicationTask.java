package com.fast.build.helper.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationTask {

    /**
     * 应用配置
     */
    private Map<String, ApplicationGitInfo> applications;


}
