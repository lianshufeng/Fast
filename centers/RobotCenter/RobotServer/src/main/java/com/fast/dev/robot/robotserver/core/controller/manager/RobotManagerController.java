package com.fast.dev.robot.robotserver.core.controller.manager;

import com.fast.dev.core.util.result.InvokerResult;
import com.fast.dev.robot.robotserver.core.model.ValidatePhoneTokenRet;
import com.fast.dev.robot.robotserver.core.model.VerifyPhoneModel;
import com.fast.dev.robot.robotserver.core.service.PhoneValidateService;
import com.fast.dev.robot.service.model.RobotValidateRet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("manager")
public class RobotManagerController extends RpcRobotManagerController {


    @Autowired
    private PhoneValidateService phoneValidateService;


    /**
     * 发送手机验证码
     *
     * @return
     */
    @RequestMapping("sendPhoneCode")
    public InvokerResult<ValidatePhoneTokenRet> sendPhoneCode(@Validated VerifyPhoneModel verifyPhoneModel) {
        return InvokerResult.notNull(this.phoneValidateService.sendPhoneCode(verifyPhoneModel));
    }


    /**
     * 校验手机验证码是否正确
     *
     * @param token
     * @param code
     * @return
     */
    @RequestMapping("verifyPhoneCode")
    public InvokerResult<RobotValidateRet> verifyPhoneCode(String token, String code) {
        return InvokerResult.notNull(super.verifyPhoneCode(token, code));
    }


}
