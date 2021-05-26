package com.fast.dev.pay.client.type;

import com.fast.dev.pay.client.support.BaseCallBack;
import com.fast.dev.pay.client.support.BasePaySupport;
import com.fast.dev.pay.client.support.alipay.AliPayAppOrder;
import com.fast.dev.pay.client.support.alipay.AliPayFaceToFaceOrder;
import com.fast.dev.pay.client.support.alipay.callback.AliPayCallBack;
import com.fast.dev.pay.client.support.cpcn.CPCNOrder;
import com.fast.dev.pay.client.support.cpcn.CPCNPayOrder;
import com.fast.dev.pay.client.support.weixin.WeiXinAppOrder;
import com.fast.dev.pay.client.support.weixin.WeiXinH5Order;
import com.fast.dev.pay.client.support.weixin.WeiXinJsApiOrder;
import com.fast.dev.pay.client.support.weixin.WeiXinNativeOrder;
import com.fast.dev.pay.client.support.weixin.callback.WeiXinCallBack;
import lombok.Getter;

/**
 * 支付方式
 */
public enum PayMethod {


    WeiXinApp("微信app支付", WeiXinAppOrder.class, WeiXinCallBack.class, AccountType.WeiXinPay),
    WeiXinJsApi("微信jsapi支付", WeiXinJsApiOrder.class, WeiXinCallBack.class, AccountType.WeiXinPay),
    WeiXinNative("微信native支付", WeiXinNativeOrder.class, WeiXinCallBack.class, AccountType.WeiXinPay),
    WeiXinH5("微信H5支付", WeiXinH5Order.class, WeiXinCallBack.class, AccountType.WeiXinPay),


    AliPayFaceToFace("支付宝面对面支付", AliPayFaceToFaceOrder.class, AliPayCallBack.class, AccountType.AliPay),
    AliPayApp("支付宝App支付", AliPayAppOrder.class, AliPayCallBack.class, AccountType.AliPay),

    CPCNFastPay("中金快捷支付", CPCNPayOrder.class, null, AccountType.CPCNPay),
    CPCNOrderPay("中金跳转支付", CPCNOrder.class, null, AccountType.CPCNOrderPay),


    ;

    // 备注

    private String remark;

    //通信数据模型
    @Getter
    private Class<? extends BasePaySupport> support;


    //回调方法
    @Getter
    private Class<? extends BaseCallBack> callBack;

    //账户类型
    @Getter
    private AccountType accountType;


    PayMethod(String remark, Class<? extends BasePaySupport> support, Class<? extends BaseCallBack> callBack, AccountType accountType) {
        this.remark = remark;
        this.support = support;
        this.callBack = callBack;
        this.accountType = accountType;
    }
}
