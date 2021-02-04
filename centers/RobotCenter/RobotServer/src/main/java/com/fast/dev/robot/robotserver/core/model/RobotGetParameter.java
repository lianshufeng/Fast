package com.fast.dev.robot.robotserver.core.model;

import com.fast.dev.robot.service.type.RobotType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RobotGetParameter {

    //业务名
    @NotNull(message = "业务名不能为空")
    private String serviceName;

    //验证码类型
    @NotNull(message = "验证码类型不能为空")
    private RobotType robotType;

    /**
     * 宽度
     */
    @Max(value = 2000, message = "宽度不能大于2000")
    private int width = 200;

    /**
     * 高度
     */
    @Max(value = 2000, message = "高度不能大于2000")
    private int height = 200;


}
