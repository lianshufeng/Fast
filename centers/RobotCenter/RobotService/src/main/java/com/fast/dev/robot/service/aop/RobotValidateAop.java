package com.fast.dev.robot.service.aop;

import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.core.util.result.InvokerState;
import com.fast.dev.robot.service.annotations.RobotValidateMethod;
import com.fast.dev.robot.service.conf.RobotFirewallConfig;
import com.fast.dev.robot.service.helper.RobotServiceHelper;
import com.fast.dev.robot.service.inter.RobotTokenInterceptor;
import groovy.util.logging.Log;
import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;


@Log
@Aspect
@Component
public class RobotValidateAop {

    @Autowired
    private RobotTokenInterceptor robotTokenInterceptor;

    @Autowired
    private RobotServiceHelper robotServiceHelper;

    @Autowired
    private RobotFirewallConfig robotFirewallConfig;

    @Pointcut("@annotation(com.fast.dev.robot.service.annotations.RobotValidateMethod)")
    public void methodPoint() {

    }


    @Around(value = "methodPoint()")
    public Object afterMethod(ProceedingJoinPoint point) throws Throwable {
        //取出方法
        Method method = getMethod(point);
        //取出注解
        RobotValidateMethod robotValidateMethod = method.getAnnotation(RobotValidateMethod.class);


        //检查机器人
        if (!validateRobotCode(robotValidateMethod)) {
            return refreshRobot(robotValidateMethod);
        }


        return point.proceed();
    }


    /**
     * 校验当前的机器码
     *
     * @return
     */
    private boolean validateRobotCode(RobotValidateMethod robotValidateMethod) {
        String token = getRequestParm(robotValidateMethod.parmRobotTokenName());
        String code = getRequestParm(robotValidateMethod.parmRobotCode());

        //如果未空
        if (!StringUtils.hasText(token) || !StringUtils.hasText(code)) {
            return false;
        }

        return this.robotServiceHelper.validate(robotValidateMethod.serviceName(), robotValidateMethod.robotType(), new HashMap<String, Object>() {{
            put("token", token);
            put("code", code);
        }});
    }


    /**
     * 刷新到机器人
     *
     * @return
     */
    private InvokerResult<?> refreshRobot(RobotValidateMethod robotValidateMethod) {
        String url = String.format("/%s/image?serviceName=%s&robotType=%s&_t=%s", this.robotFirewallConfig.getRobotServerName(), robotValidateMethod.serviceName(), robotValidateMethod.robotType(), String.valueOf(System.currentTimeMillis()));
        return InvokerResult.builder().state(InvokerState.Robot).content(new HashMap<String, Object>() {{
            put("url", url);
        }}).build();
    }


    /**
     * 获取请求的参数,通过head与参数
     *
     * @param parmName
     * @return
     */
    private String getRequestParm(String parmName) {
        HttpServletRequest request = this.robotTokenInterceptor.getCurrentRequest().get();
        String value = request.getHeader(parmName);
        if (!StringUtils.hasText(value)) {
            value = request.getParameter(parmName);
        }
        return value;
    }


    @SneakyThrows
    private Method getMethod(JoinPoint point) {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        return methodSignature.getMethod();
    }
}
