package com.fast.dev.pay.server.core.hb.type;

import lombok.Getter;

/**
 * 订单冻结状态
 */
public enum ContractFreezeState {

    Wait("等待冻结"),

    Fail("冻结失败"),

    Finish("冻结成功"),

    ;

    @Getter
    private String remark;

    ContractFreezeState(String remark) {
        this.remark = remark;
    }
}
