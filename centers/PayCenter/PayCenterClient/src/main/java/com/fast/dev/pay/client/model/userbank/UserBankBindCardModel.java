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
public class UserBankBindCardModel extends UserBankPreBindCardModel {

    //手机号码
    private String phoneNumber;

}
