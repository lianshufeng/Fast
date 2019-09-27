package com.fast.dev.robot.robotserver.core.controller;

import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.robot.robotserver.core.model.RobotParameter;
import com.fast.dev.robot.robotserver.core.model.RobotValidate;
import com.fast.dev.robot.robotserver.core.service.ImageTemplateService;
import com.netflix.discovery.converters.Auto;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping
public class RobotController {

    @Autowired
    private ImageTemplateService imageTemplateService;

    /**
     * 获取验证码
     *
     * @return
     */
    @RequestMapping("get")
    public InvokerResult<Object> get(@Validated RobotParameter robotParameter, HttpServletResponse response) {
        ByteOutputStream byteOutputStream = new ByteOutputStream();
        RobotValidate robotValidate = this.imageTemplateService.build(robotParameter, byteOutputStream);


        return InvokerResult.notNull(robotValidate);
    }

    /**
     * 校验
     *
     * @return
     */
    @RequestMapping("validate")
    public InvokerResult<Object> validate(@Validated RobotParameter robotParameter) {

        return null;
    }


}
