package com.fast.dev.pay.server.core.hb.type;

public enum  ApiType {

    /**
     * 申请开户
     */
    OpenAcct("OpenAcct"),

    /**
     * 确认开户
     */
    ConfrimOpenAcct("ConfrimOpenAcct"),

    /**
     * 开户信息查询
     */
    QueryOpenAcct("QueryOpenAcct"),

    /**
     * 冻结/解冻
     */
    FreezeAcct("FreezeAcct"),


    /**
     * 解冻支付/退款
     */
    ThawPayRefund("ThawPayRefund"),


    QueryTransResult("QueryTransResult")
    ;

    private String url;

    ApiType(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}
