package com.fast.dev.pay.client.model.sign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求的签名模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqSignModel {

    //支付账号id
    private String payAccountId;

    //需要签名的数据
    private String[] parm;

}
