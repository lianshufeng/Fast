package com.fast.components.robotfirewall.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 机器校验码
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RobotValidate {

    /**
     * 校验的表达式， 使用springel表达式校验
     */
    private String expression;


}
