package com.fast.dev.pay.server.core.hb.type;

import lombok.Getter;

public enum ChannelName {

    AppPay("手机app等移动端(移动支付)", 01),
    PCPay("PC网站(网上支付)", 02),
    Phone("电话", 03),
    SmartPhone("智能电话", 04),
    Counter("柜面", 05),
    Other("其他", 99),

    ;
    @Getter
    private String remark;

    @Getter
    private int code;

    ChannelName(String remark, int code) {
        this.remark = remark;
        this.code = code;
    }
}
