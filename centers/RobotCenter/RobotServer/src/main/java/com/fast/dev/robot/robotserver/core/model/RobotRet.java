package com.fast.dev.robot.robotserver.core.model;

import com.fast.dev.robot.service.type.RobotType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RobotRet {


    //业务名
    private String serviceName;

    //token
    private String token;

    //图片
    private String image;

    //验证码类型
    private RobotType robotType;


}
