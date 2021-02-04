package com.fast.dev.pay.server.core.hb.type;

import lombok.Getter;

/**
 * 自动扣款的状态
 */
public enum ContractState {

    WaitFreeze("等待冻结"),
    WaitCharge("等待扣款"),
    Finish("完成合同"),
    Cancel("终止合同"),

    ;

    @Getter
    private String remark;

    ContractState(String remark) {
        this.remark = remark;
    }
}
