package com.fast.dev.pay.server.core.general.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliConfigModel {

    //支付宝支付的地址, 正式的为： openapi.alipay.com
    private String gatewayHost = "openapi.alipaydev.com";


    //通知地址
    private String notifyUrl;

}
