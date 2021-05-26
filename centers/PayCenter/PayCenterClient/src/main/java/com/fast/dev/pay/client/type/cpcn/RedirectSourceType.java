package com.fast.dev.pay.client.type.cpcn;

import lombok.Getter;

@Getter
public enum RedirectSourceType {


    App("10","App"),
    H5("20","H5"),
    OfficialAccounts("30","公众号"),
    WechatMiniProgram("40","小程序");

    private String code;

    private String remark;

    RedirectSourceType(String code, String remark){
        this.code = code;
        this.remark = remark;
    }
}
