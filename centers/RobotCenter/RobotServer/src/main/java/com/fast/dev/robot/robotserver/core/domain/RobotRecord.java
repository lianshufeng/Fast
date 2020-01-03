package com.fast.dev.robot.robotserver.core.domain;

import com.fast.dev.robot.service.type.RobotType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@ToString(callSuper = true)
@Document
@AllArgsConstructor
@NoArgsConstructor
public class RobotRecord extends TokenRecord {

    //业务名
    @Indexed
    private String serviceName;

    //机器人类型
    @Indexed
    private RobotType robotType;


    //校验的表达式， 使用springel表达式校验
    private String expression;


}
