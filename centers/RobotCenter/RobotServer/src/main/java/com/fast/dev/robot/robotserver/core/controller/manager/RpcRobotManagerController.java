package com.fast.dev.robot.robotserver.core.controller.manager;

import com.fast.dev.robot.robotserver.core.model.VerifyPhoneModel;
import com.fast.dev.robot.robotserver.core.service.PhoneValidateService;
import com.fast.dev.robot.service.model.RobotValidateRet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rpc/manager")
public class RpcRobotManagerController {


    @Autowired
    private PhoneValidateService phoneValidateService;


    /**
     * 发送手机验证码
     *
     * @return
     */
    @RequestMapping("sendPhoneCode")
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
