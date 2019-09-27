package com.fast.dev.robot.robotserver.core.factory;

import com.fast.dev.robot.robotserver.core.model.RobotParm;
import com.fast.dev.robot.robotserver.core.model.RobotValidate;
import com.fast.dev.robot.service.type.RobotType;
import org.springframework.beans.BeanUtils;

import java.io.InputStream;
import java.io.OutputStream;


/**
 * 校验码生成接口
 */
public abstract class RobotFactory {

    /**
     * 校验类型
     *
     * @return
     */
    public abstract RobotType robotType();


    /**
     * 生成验证码
     *
     * @param templateInputStream
     * @param dataOutputStream
     * @param robotParm
     * @return
     */
    public abstract RobotValidate build(InputStream templateInputStream, OutputStream dataOutputStream, RobotParm robotParm);


    /**
     * 获取默认的参数
     *
     * @return
     */
    public abstract RobotParm getDefaultBuildInfo();


    /**
     * 获取robot的参数
     *
     * @param robotParm
     * @return
     */
    public RobotParm getRobotParm(RobotParm robotParm) {
        RobotParm ret = getDefaultBuildInfo();
        if (robotParm != null) {
            BeanUtils.copyProperties(robotParm, ret);
        }
        return ret;
    }


}
