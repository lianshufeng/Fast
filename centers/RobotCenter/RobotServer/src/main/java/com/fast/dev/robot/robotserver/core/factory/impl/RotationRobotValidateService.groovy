package com.fast.dev.robot.robotserver.core.factory.impl

import com.fast.dev.robot.robotserver.core.factory.RobotFactory
import com.fast.dev.robot.robotserver.core.model.RobotParm
import com.fast.dev.robot.robotserver.core.model.RobotValidate
import com.fast.dev.robot.robotserver.core.util.ImageConvert
import com.fast.dev.robot.service.type.RobotType
import org.apache.commons.lang3.RandomUtils
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils

/**
 * 旋转的验证码生成器
 */
@Component
@Scope("prototype")
public class RotationRobotValidateService extends RobotFactory {

    //表达式
    private final static String expression = "T(java.lang.Math).abs(%s - T(java.lang.Math).abs(#code)) <=15";

    //变量
    private final static Set<String> variableNames = new HashSet<String>() {
        {
            add("code");
        }
    };


    @Override
    RobotParm getDefaultBuildInfo() {
        return new RobotParm(200, 200)
    }

    @Override
    RobotType robotType() {
        return RobotType.Rotation
    }

    @Override
    RobotValidate build(InputStream templateInputStream, OutputStream dataOutputStream, RobotParm robotParm) {
        RobotParm parm = getRobotParm(robotParm)

        //随机数，旋转的角度
        int angel = RandomUtils.nextInt(30, 330);

        ImageConvert imageConvert = new ImageConvert(templateInputStream)

        //图形转换
        InputStream imageStream = imageConvert
                .resize(parm.getWidth(), parm.getHeight())
                .transferImgForRoundImgage(angel)
                .getInputStream()

        //拷贝到输出流
        StreamUtils.copy(imageStream, dataOutputStream)

        RobotValidate robotValidate = new RobotValidate()
        robotValidate.setExpression(String.format(expression, String.valueOf(angel)))
        robotValidate.setVariableNames(variableNames)
        robotValidate.setRobotType(robotType())

        return robotValidate


    }
}
