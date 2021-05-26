package com.fast.dev.pay.client.type.cpcn;

import lombok.Getter;

@Getter
public enum PayWay {


    H5Pay("45","H5支付"),
    AppPay("46","APP支付"),
    WechatMiniProgramPay("51","小程序支付");

    private String code;

    private String remark;

    PayWay(String code, String remark){
        this.code = code;
        this.remark = remark;
    }
}
