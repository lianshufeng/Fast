package com.fast.dev.ucenter.core.type;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 * 用户令牌状态
 */
public enum TokenState {

    Success("成功"),

    Error("失败"),

    CreateError("令牌创建失败"),

    UserNotExist("用户不存在"),

    UserNotNull("用户不能为空"),

    UserExist("用户已存在"),

    PassWordError("密码错误"),

    TokenNotExist("令牌不存在"),

    NotSupportType("不支持的类型"),

    TokenNotMatch("令牌不匹配"),

    TokenMaxLimit("令牌超过最大的访问限制"),

    ValidataCodeError("验证码不正确"),
    ;
    private String remark;

    TokenState(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }
}
