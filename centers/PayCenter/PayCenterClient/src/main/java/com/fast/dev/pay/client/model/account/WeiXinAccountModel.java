package com.fast.dev.pay.client.model.account;

import com.fast.dev.pay.client.annotations.ValidateCert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeiXinAccountModel extends SuperAccount {

    //直连商户号
    private String mchid;
    //apiv3的秘钥
    private String apiV3Key;

    //api证书数据,base64编码
    @ValidateCert
    private String apiCertData;
    //api证书秘钥,base64编码
    private String apiCertPrivateKey;

    //api的p12证书，微信支付v2需要
    private String apiCertP12;

    //公众号ID
    private String appid;

    //秘钥
    private String appSecret;

    //商户号秘钥
    private String mchSecret;
}
