package com.fast.dev.gateway.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 策略
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Policy {

    //周期时间,单位秒
    private long cycleTime;

    //访问次数
    private long accessCount;

    //阻塞时间，单位秒
    private long blockTime;

}
