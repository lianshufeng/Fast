package com.fast.dev.gateway.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 限制规则
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LimitRole {

    /**
     * 需要限制的url
     */
    private String[] url;

    /**
     * 限制中的排除
     */
    private String[] exclusion;

    /**
     * 允许访问的ip,仅支持ipv4
     */
    private String[] allowIp;

    /**
     * 限制策略
     */
    private Policy policy;

}
