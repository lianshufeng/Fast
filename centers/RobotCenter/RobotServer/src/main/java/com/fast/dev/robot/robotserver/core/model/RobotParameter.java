package com.fast.dev.robot.robotserver.core.model;

import com.fast.dev.robot.service.type.RobotType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RobotParameter {

    //业务名
    @NotNull(message = "业务名不能为空")
    private String serviceName;

    //验证码类型
    @NotNull(message = "验证码类型不能为空")
    private RobotType robotType;


}
