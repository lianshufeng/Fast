package com.fast.dev.core.util.result.content;

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


    ;

    @Getter
    private String remark;

    ResultState(String remark) {
        this.remark = remark;
    }
}
