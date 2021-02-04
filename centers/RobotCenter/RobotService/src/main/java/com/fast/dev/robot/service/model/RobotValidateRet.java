package com.fast.dev.robot.service.model;

import com.fast.dev.robot.service.type.RobotType;
import com.fast.dev.robot.service.type.ValidateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 机器请求参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RobotValidateRet {

    //校验码类型
    private RobotType robotType;

    //校验结果
    private ValidateType validateType;

    //业务名
    private String serviceName;


}
