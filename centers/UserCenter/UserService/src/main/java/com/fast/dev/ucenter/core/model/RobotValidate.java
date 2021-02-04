package com.fast.dev.ucenter.core.model;

import com.fast.dev.ucenter.core.type.ValidateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 * 登陆校验
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RobotValidate {

    /**
     * 校验类型
     */
    private ValidateType type;


    /**
     * 需要验证的数据，
     * 若为none,则为具体的登陆码
     * 若为Image,则为图片的base64编码
     * 若为Phone,则为null(开发模式下为验证码)
     *
     */
    private String data;



    public RobotValidate(ValidateType type) {
        this.type = type;
    }
}
