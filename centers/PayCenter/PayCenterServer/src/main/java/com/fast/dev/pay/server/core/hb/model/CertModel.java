package com.fast.dev.pay.server.core.hb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 证书模型
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CertModel {

    //store至少6位
    private String storePassWord;

    //秘钥长度
    private int keySize = 2048;

    //别名
    private String alias;

    //秘钥类型
    private String keyalg = "RSA";

    //名与姓
    private String firstLastName;

    //企业,单位名称全称
    private String organizational;

    //单位部门名称全称
    private String organization;

    //城市或区域名称,BeiJing
    private String cityLocality;

    //州或省份名称
    private String province;

    //两字母国家代码，必须是CN
    private String countryCode = "CN";

    //证书有效期
    private int validity = 365;


}
