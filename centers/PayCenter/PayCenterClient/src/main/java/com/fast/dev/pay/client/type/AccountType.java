package com.fast.dev.pay.client.type;

import com.fast.dev.pay.client.model.account.AliPayAccountModel;
import com.fast.dev.pay.client.model.account.CPCNAccountModel;
import com.fast.dev.pay.client.model.account.SuperAccount;
import com.fast.dev.pay.client.model.account.WeiXinAccountModel;
import lombok.Getter;

/**
 * 支付的账户类型
 */
public enum AccountType {


    WeiXinPay("微信支付", "wx", WeiXinAccountModel.class),
    AliPay("支付宝", "al", AliPayAccountModel.class),
    CPCNPay("中金快捷支付", "cp", CPCNAccountModel.class),
    CPCNOrderPay("中金跳转支付", "co", CPCNAccountModel.class),
    ;

    AccountType(String name, String code, Class<? extends SuperAccount> type) {
        this.name = name;
        this.code = code;
        this.type = type;
    }

    @Getter
    private String name;

    @Getter
    private String code;

    @Getter
    private Class<? extends SuperAccount> type;

}
