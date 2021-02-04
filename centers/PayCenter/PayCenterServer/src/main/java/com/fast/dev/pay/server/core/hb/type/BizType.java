package com.fast.dev.pay.server.core.hb.type;

import lombok.Getter;

/**
 * 业务类型
 */
public enum  BizType {

    FREEZE("冻结"),

    THAW("解冻"),

    THAWPAY("解冻支付"),

    THAWREFUND("解冻退款"),

    OPENACCT("开户")

    ;


    @Getter
    private String remark;

    BizType(String remark) {
        this.remark = remark;
    }


}
