package com.fast.dev.pay.client.model.userbank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBankCardModel {

    private String id;

    //用户id
    private String uid;

    //账户名称
    private String accountName;

    //账户号码
    private String accountNumber;

    //身份证
    private String identificationNumber;

    //手机号码
    private String phoneNumber;

    //是否删除
    private boolean delete;

    //创建时间
    private long createTime;

}
