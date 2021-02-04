package com.fast.dev.pay.client.support.weixin;

import com.fast.dev.pay.client.support.weixin.model.WeiXinPayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * app创建订单
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeiXinJsApiOrder extends WeiXinBaseOrder {


    @Override
    public String getUrl() {
        return "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";
    }


    //支付者
    private WeiXinPayer payer;



}
