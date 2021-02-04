package com.fast.dev.pay.server.core.hb.type;

import lombok.Getter;

/**
 * 账户类型
 */
public enum AcctType {
    DEBIT("借记账户","1"),
    CREDIT("贷记账户","2"),
    PUBLIC("银行对公账户","3");

    @Getter
    private String remark;

    @Getter
    private String code;
    AcctType(String remark,String code) {
        this.remark = remark;
        this.code = code;
    }
}
