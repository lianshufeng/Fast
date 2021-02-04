package com.fast.dev.ucenter.security.resauth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceInfo {

    //资源名
    private String name;

    //描述
    private String remark;

}
