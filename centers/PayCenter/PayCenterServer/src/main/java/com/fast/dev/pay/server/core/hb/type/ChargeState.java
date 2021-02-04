package com.fast.dev.pay.server.core.hb.type;

import lombok.Getter;

public enum ChargeState {


    Success("扣款成功"),
    Fail("扣款失败"),
    WaitCheck("等待验证"),

    ;

    @Getter
    private String remark;

    ChargeState(String remark) {
        this.remark = remark;
    }

}
