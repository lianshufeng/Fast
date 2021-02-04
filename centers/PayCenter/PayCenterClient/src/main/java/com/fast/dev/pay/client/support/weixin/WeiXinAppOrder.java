package com.fast.dev.pay.client.support.weixin;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * app创建订单
 */
@Data
@NoArgsConstructor
public class WeiXinAppOrder extends WeiXinBaseOrder {

    @Override
    public String getUrl() {
        return "https://api.mch.weixin.qq.com/v3/pay/transactions/app";
    }


}
