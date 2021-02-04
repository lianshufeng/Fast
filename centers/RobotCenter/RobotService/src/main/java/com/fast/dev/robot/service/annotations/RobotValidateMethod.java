package com.fast.dev.robot.service.annotations;

import com.fast.dev.robot.service.type.RobotType;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 机器人校验方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RobotValidateMethod {
    /**
     * 缓存数据集名，支持多个
     *
     * @return
     */
    String serviceName();


    /**
     * 机器校验类型
     *
     * @return
     */
    RobotType robotType() default RobotType.Rotation;


    /**
     * token的名称
     *
     * @return
     */
    String parmRobotTokenName() default "robotToken";


    /**
     * token的校验码
     *
     * @return
     */
    String parmRobotCode() default "robotCode";

}
