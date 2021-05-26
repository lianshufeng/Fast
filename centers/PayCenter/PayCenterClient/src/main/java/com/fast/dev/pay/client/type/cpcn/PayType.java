package com.fast.dev.pay.client.type.cpcn;

import lombok.Getter;

@Getter
public enum  PayType {


    WeChat("31","微信"),
    ZFB("32","支付宝");

    private String code;

    private String remark;

    PayType(String code, String remark){
        this.code = code;
        this.remark = remark;
    }
}
