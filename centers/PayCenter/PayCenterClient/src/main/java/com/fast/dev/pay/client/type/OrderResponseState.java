package com.fast.dev.pay.client.type;


import lombok.Getter;

public enum OrderResponseState {

    Success("成功"),
    Error("失败"),
    Exception("异常"),
    Robot("机器验证"),


    ;

    @Getter
    private String remark;


    OrderResponseState(String remark) {
        this.remark = remark;
    }

}