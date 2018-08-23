package com.fast.dev.ucenter.core.type;

/**
 * 作者：练书锋
 * 时间：2018/8/22
 *  用户令牌状态
 */
public enum TokenState {

    Success("成功") ,

    CreateError("令牌创建失败") ,

    UserNotExist("用户不存在"),


    UserExist("用户已存在"),


    PassWordError("密码错误"),


    TokenNotExist("令牌不存在"),

    NotSupportType("不支持的类型"),

    ;
    private String remark;

    TokenState(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }
}
