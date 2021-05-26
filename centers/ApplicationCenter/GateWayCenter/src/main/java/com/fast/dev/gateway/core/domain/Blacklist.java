package com.fast.dev.gateway.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 黑名单
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Blacklist {

    //ip地址
    private String ip;

    //角色名
    private String roleName;

    //创建时间
    private long createTime;

}
