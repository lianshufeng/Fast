package com.fast.dev.ucenter.core.type;

/**
 * 作者：练书锋
 * 时间：2018/8/28
 * 用户消息类型
 */
public enum UserMessageType {

    /**
     * 注册
     */
    Register("注册"),

    /**
     * 登陆
     */
    Login("登陆"),

    /**
     * 注销
     */
    Logout("注销"),

    /**
     * 禁用
     */
    Enable("启用"),


    /**
     * 禁用
     */
    Disable("禁用"),


    /**
     * 清空缓存
     */
    CleanCache("清空缓存"),


    /**
     * 更新登录名
     */
    UpdateLoginName("更新登录名"),

    ;

    UserMessageType(String remark) {
        this.remark = remark;
    }

    private String remark;

    public String getRemark() {
        return remark;
    }
}
