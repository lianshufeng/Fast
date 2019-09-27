package com.fast.dev.robot.robotserver.core.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.robot.robotserver.core.model.RobotValidate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class RobotRecord extends SuperEntity {

    @Indexed(unique = true)
    private String token;

    //机器校验码
    private RobotValidate robotValidate;


}
