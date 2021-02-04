package com.fast.build.helper.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTaskResp {

    //项目名
    private String[] projectName;

    //创建时间
    private long createTime;
}
