package com.fast.dev.robot.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RobotValidateParameter {

    //业务名
    @NotNull(message = "令牌不能为空")
    private String token;


}
