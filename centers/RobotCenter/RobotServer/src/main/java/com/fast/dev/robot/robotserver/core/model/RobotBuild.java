package com.fast.dev.robot.robotserver.core.model;

import com.fast.dev.robot.service.type.RobotType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RobotBuild {


    private String token;

    //业务名
    private String serviceName;

    //机器人类型
    private RobotType robotType;

}
