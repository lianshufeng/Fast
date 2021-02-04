package com.fast.dev.robot.robotserver.core.model;

import com.fast.dev.robot.service.type.RobotType;
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

    //校验的表达式， 使用springel表达式校验
    private String expression;


    //机器人类型
    private RobotType robotType;


}
