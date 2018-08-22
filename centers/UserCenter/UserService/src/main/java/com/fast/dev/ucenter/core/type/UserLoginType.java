package com.fast.dev.ucenter.core.type;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 */
public enum UserLoginType {

    /**
     * 自定义用户登陆
     */
    UserName("用户名"),

    /**
     * 手机登陆
     */
    Phone("手机"),


    /**
     * 微信登陆
     */
    WeiXin("微信"),


    /**
     * QQ登陆
     */
    QQ("QQ");


    private String remark;

    UserLoginType(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }
}
