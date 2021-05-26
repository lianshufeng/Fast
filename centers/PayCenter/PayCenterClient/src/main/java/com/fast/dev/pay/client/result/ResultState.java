package com.fast.dev.pay.client.result;

import lombok.Getter;

/**
 * 结果状态模板
 */
public enum ResultState {

    Success("成功"),
    Fail("失败"),
    Error("错误"),
    Exception("异常"),
    Robot("机器验证"),

    RepeatOrderCallBack("重复的订单回调"),

    OrderError("订单错误"),
    OrderNotExist("订单不存在"),
    OrderExist("订单存在"),
    OrderNotValidTime("订单不在有效的时间内"),
    OrderIsUsed("订单已使用"),
    OrderRepeat("订单重复,已购买过"),
    OrderPaid("订单已支付"),
    OrderProcess("订单处理中"),

    RefundError("退款失败"),

    NotPaySupport("未实现的支付"),
    UserNotExist("用户不存在"),
    EnterpriseExist("企业已存在"),
    EnterpriseNotExist("企业不存在"),
    EnterprisePayAccountNotExist("企业支付账号不存在"),
    EnterprisePayAccountDisable("企业支付账号被禁用"),
    EnterprisePayAccountTypeError("企业支付账号类型错误"),

    PaySupportBuildParameterError("第三方参数构建失败"),
    PaySupporOrderQueryError("支付平台订单查询失败"),

    ResponseVerifySignatureError("响应的签名验证错误"),

    MchIdNotNull("商户id不能为空"),
    MchNoNotNull("商户号不能为空"),
    AppIdNotNull("应用id不能为空"),
    PhoneNotNull("手机号码不能为空"),
    EnterpriseNameNotNull("企业名不能为空"),
    EnterpriseRegisterError("企业注册失败"),
    EnterpriseRegisterExist("企业已注册"),

    HuaXiaEnterpriseIdError("华夏企业id错误"),

    TemplateNotExist("模板不存在"),
    TemplateIsDisable("模板被禁用"),
    TemplateNotValidTime("模板不在有效的时间内"),

    validateMessageFail("验证短信失败"),


    PreBindCardExist("预绑定银行卡信息已存在"),
    PreBindCardNoExist("预绑定银行卡信息不存在"),
    UserBankCardExist("用户银行卡已存在"),
    UserBankCardNotExist("用户银行卡不存在"),
    UserBankCardBindError("用户银行卡绑定失败"),


    ;

    @Getter
    private String remark;

    ResultState(String remark) {
        this.remark = remark;
    }
}
