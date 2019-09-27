package com.fast.dev.getway.core.model;

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
     * 允许访问的ip
     */
    private String[] allowIp;

}
