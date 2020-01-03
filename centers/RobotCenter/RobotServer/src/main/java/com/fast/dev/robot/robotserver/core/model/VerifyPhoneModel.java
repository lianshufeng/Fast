package com.fast.dev.robot.robotserver.core.model;

import com.fast.dev.robot.robotserver.core.type.PhoneCodeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * 校验手机的参数模型
 */


@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class VerifyPhoneModel {

    //手机号码
    @NotNull(message = "手机号码不能为空")
    private String phone;

    //短信的模板id
    @NotNull(message = "短信模板不能为空")
    private String templateId;

    //过期时间，单位秒
    private long timeOut = 1000 * 60 * 5;

    //限制的长度
    private int codeSize = 4;

    //最大尝试次数
    private int tryCount = 3;


    //code的生成规则
    private PhoneCodeType phoneCodeType = PhoneCodeType.Number;


}
