package com.fast.dev.pay.server.core.general.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeiXinConfigModel {

//    //直连商户号
//    private String mchid;
//    //apiv3的秘钥
//    private String apiV3Key;
//
//    //api证书数据,base64编码
//    private String apiCertData;
//    //api证书秘钥,base64编码
//    private String apiCertPrivateKey;
//
//    //公众号ID
//    private String appid;
//
//    //秘钥
//    private String appSecret;
//
//    //商户号秘钥
//    private String mchSecret;


    //通知地址
    private String notifyUrl;

}
