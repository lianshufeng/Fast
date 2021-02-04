package com.fast.dev.pushcenter.manager.type;

/**
 * 作者：练书锋
 * 时间：2018/9/3
 * <p>
 * 消息类型
 */
public enum MessageType {

    Sms("短信"),
    Mail("邮件"),
    App("应用");
    private String remark;

    MessageType(String remark) {
        this.remark = remark;
    }


}
