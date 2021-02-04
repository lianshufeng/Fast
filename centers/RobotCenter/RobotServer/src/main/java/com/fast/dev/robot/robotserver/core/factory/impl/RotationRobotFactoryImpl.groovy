package com.fast.dev.robot.robotserver.core.factory.impl


import com.fast.dev.robot.robotserver.core.factory.RobotFactory
import com.fast.dev.robot.robotserver.core.factory.model.InputSource
import com.fast.dev.robot.robotserver.core.model.RobotParm
import com.fast.dev.robot.robotserver.core.model.RobotValidate
import com.fast.dev.robot.robotserver.core.util.ImageHelper
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
public class RotationRobotFactoryImpl extends RobotFactory {


    //表达式
    private final static String expression = "T(java.lang.Math).abs(%s - T(java.lang.Math).abs( T(java.lang.Double).parseDouble(#code) )) <= %s";


    @Override
    RobotParm getDefaultBuildInfo() {
        return new RobotParm(200, 200)
    }

    @Override
    RobotType robotType() {
        return RobotType.Rotation
    }

    @Override
    RobotValidate build(InputSource inputSource, OutputStream dataOutputStream, RobotParm robotParm) {
        RobotParm parm = getRobotParm(robotParm)

        //随机数，旋转的角度
        int angel = RandomUtils.nextInt(30, 330);


        ImageHelper imageConvert = new ImageHelper(inputSource.getTemplateInputStream())

        //图形转换角度
        InputStream imageStream = imageConvert
                .scale(parm.getWidth() * 2, parm.getHeight() * 2) //高清
                .rotate(angel) //旋转
                .scale(parm.getWidth(), parm.getHeight()) //缩放
                .getInputStream()


        //拷贝到输出流
        StreamUtils.copy(imageStream, dataOutputStream)


        RobotValidate robotValidate = new RobotValidate()
        //容错值,四舍五入保留两位小数
        float errorCode = new BigDecimal(360 * robotFirewallConfig.getErrorRate()).setScale(2, BigDecimal.ROUND_HALF_UP)


        //设置表达式  -  角度  - 容错角度
        robotValidate.setExpression(String.format(expression, String.valueOf(angel), String.valueOf(errorCode)))
        robotValidate.setRobotType(robotType())

        return robotValidate


    }
}
