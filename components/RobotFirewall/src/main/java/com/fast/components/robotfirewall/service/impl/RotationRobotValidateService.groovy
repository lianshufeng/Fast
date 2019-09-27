package com.fast.components.robotfirewall.service.impl

import com.fast.components.robotfirewall.model.RobotParm
import com.fast.components.robotfirewall.model.RobotValidate
import com.fast.components.robotfirewall.service.RobotValidateService
import com.fast.components.robotfirewall.type.RobotType
import com.fast.components.robotfirewall.util.ImageConvert
import org.apache.commons.lang3.RandomUtils
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils

import java.util.stream.Stream

/**
 * 旋转的验证码生成器
 */
@Component
@Scope("prototype")
public class RotationRobotValidateService extends RobotValidateService {

    //表达式
    private final static String expression = "T(java.lang.Math).abs(%s - T(java.lang.Math).abs(#code)) <=15 && #time > 0 ";

    @Override
    RobotParm getDefaultBuildInfo() {
        RobotParm robotParm = new RobotParm()
        robotParm.width = 200
        robotParm.height = 200
        return robotParm
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

        return RobotValidate.builder().expression(String.format(expression, String.valueOf(angel))).build()

    }
}
