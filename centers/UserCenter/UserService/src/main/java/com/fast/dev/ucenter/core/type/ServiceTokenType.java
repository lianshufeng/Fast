package com.fast.dev.ucenter.core.type;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 * 业务令牌类型
 */
public enum ServiceTokenType {

    /**
     * 手机登录
     */
    PhoneLogin("手机登陆"),

    /**
     * 用户名登陆
     */
    UserNameLogin("用户名登陆"),

    /**
     * 用户名注册
     */
    UserNameRegister("用户名注册"),

    /**
     * 手机注册
     */
    PhoneRegister("手机注册");


    private String remark;

    ServiceTokenType(String remark) {
        this.remark = remark;
    }
}
