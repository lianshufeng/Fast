package com.fast.dev.pay.client.model.userbank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户绑卡参数
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBankPreBindCardModel {

    //用户id
    private String uid;

     //支付账号
    private String payAccountId;

    //账户名称
    private String accountName;

    //账户号码
    private String accountNumber;

    //身份证
    private String identificationNumber;

    //中金银行绑定流水号
    private String txSNBinding;

}
