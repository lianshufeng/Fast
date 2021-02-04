package com.fast.dev.openapi.client.model.v1;

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


    TimeInvalid("时间无效"),
    AkError("ak错误"),
    AkNotNull("ak不能为空"),
    DataNotNull("内容参数不能为空"),
    decryptError("解密失败"),
    InternalError("内部错误"),
    RequestNotFound("请求的接口不存在"),


    ;
    @Getter
    private String remark;

    ResultState(String remark) {
        this.remark = remark;
    }
}
