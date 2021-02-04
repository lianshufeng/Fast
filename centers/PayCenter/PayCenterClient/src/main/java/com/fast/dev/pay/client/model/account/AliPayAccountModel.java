package com.fast.dev.pay.client.model.account;

import com.fast.dev.pay.client.annotations.ValidateCert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AliPayAccountModel extends SuperAccount {

    //应用id
    private String appId;

    //私钥字符串
    private String merchantPrivateKey;

    //应用证书公钥
    @ValidateCert
    private String merchantCert;

    //支付宝公钥证书
    @ValidateCert
    private String alipayCert;

    //支付宝根证书
    private String alipayRootCert;

}
