package com.fast.dev.robot.robotserver.core.controller;

import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.robot.robotserver.core.model.RobotBuild;
import com.fast.dev.robot.robotserver.core.model.RobotGetParameter;
import com.fast.dev.robot.robotserver.core.model.RobotRet;
import com.fast.dev.robot.robotserver.core.model.VerifyPhoneModel;
import com.fast.dev.robot.robotserver.core.service.ImageTemplateService;
import com.fast.dev.robot.robotserver.core.service.PhoneValidateService;
import com.fast.dev.robot.service.annotations.RobotValidateMethod;
import com.fast.dev.robot.service.model.RobotValidateParameter;
import com.fast.dev.robot.service.model.RobotValidateRet;
import com.fast.dev.robot.service.type.RobotType;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

@RestController
@RequestMapping
@Slf4j
public class RobotController {

    @Autowired
    private ImageTemplateService imageTemplateService;


    @Autowired
    private PhoneValidateService phoneValidateService;


    /**
     * 获取验证码
     *
     * @return
     */
    @SneakyThrows
    @RequestMapping("get")
    public InvokerResult<RobotRet> get(@Validated RobotGetParameter robotParameter, HttpServletResponse response) {
        @Cleanup ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        RobotBuild robotBuild = this.imageTemplateService.build(robotParameter, byteArrayOutputStream);

        RobotRet robotRet = new RobotRet();
        BeanUtils.copyProperties(robotBuild, robotRet);

        //设置访问的图片
        robotRet.setImage("data:image/png;base64," + Base64Utils.encodeToString(byteArrayOutputStream.toByteArray()));

        return InvokerResult.notNull(robotRet);
    }


    @SneakyThrows
    @RequestMapping("image")
    public void image(@Validated RobotGetParameter robotParameter, HttpServletResponse response) {

        @Cleanup ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        RobotBuild robotBuild = this.imageTemplateService.build(robotParameter, byteArrayOutputStream);
        response.setHeader("robotToken", robotBuild.getToken());
        response.setContentType("image/jpg");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Expose-Headers", "robotToken");

        @Cleanup OutputStream outputStream = response.getOutputStream();
        StreamUtils.copy(byteArrayOutputStream.toByteArray(), outputStream);


    }


    /**
     * 校验
     *
     * @return
     */
    @RequestMapping("validate")
    public InvokerResult<RobotValidateRet> validate(HttpServletRequest request, @Validated RobotValidateParameter robotValidateParameter) {
        return InvokerResult.notNull(this.imageTemplateService.validate(request, robotValidateParameter));
    }


    /**
     * 发送手机验证码
     *
     * @return
     */
    @RequestMapping("sendPhoneCode")
    @RobotValidateMethod(serviceName = "Robot/SendPhoneCode", robotType = RobotType.Rotation)
    public Object sendPhoneCode(@Validated VerifyPhoneModel verifyPhoneModel) {
        return this.phoneValidateService.sendPhoneCode(verifyPhoneModel);
    }


    /**
     * 校验手机验证码是否正确
     *
     * @param token
     * @param code
     * @return
     */
    @RequestMapping("verifyPhoneCode")
    public Object verifyPhoneCode(String token, String code) {
        return this.phoneValidateService.verifyPhoneCode(token, code);
    }


}
