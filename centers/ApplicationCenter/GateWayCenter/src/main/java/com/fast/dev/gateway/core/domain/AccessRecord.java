package com.fast.dev.gateway.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessRecord {

    //请求
    private String ip;


    //请求头的UA
    private String ua;


    //访问的URL
    private String url;

    //访问时间
    private long time;

    //角色名
    private String roleName;


}
