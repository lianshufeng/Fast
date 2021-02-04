package com.fast.dev.pay.server.core.general.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "pay")
public class PayConf {

    //微信
    private WeiXinConfigModel weixin = new WeiXinConfigModel();

    //阿里支付
    private AliConfigModel ali = new AliConfigModel();


    //订单生效时间
    private long timeExpire = 1000 * 60 * 60;

    //签名
    private String signName = "PayCenter";


}
