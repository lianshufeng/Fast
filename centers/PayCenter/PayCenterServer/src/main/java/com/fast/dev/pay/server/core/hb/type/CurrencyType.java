package com.fast.dev.pay.server.core.hb.type;

import lombok.Getter;

/**
 * 币种
 */
public enum CurrencyType {

    CNY("人民币");

    @Getter
    private String remark;

    CurrencyType(String remark) {
        this.remark = remark;
    }
}
