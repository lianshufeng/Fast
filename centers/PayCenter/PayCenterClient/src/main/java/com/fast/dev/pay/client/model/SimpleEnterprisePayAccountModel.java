package com.fast.dev.pay.client.model;

import com.fast.dev.pay.client.type.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleEnterprisePayAccountModel {

    /**
     * 账户id
     */
    private String id;


    /**
     * 企业id
     */
    private String enterpriseId;


    /**
     * 禁用
     */
    private boolean disable;


    /**
     * 企业账户的类型
     */
    private AccountType accountType;


    /**
     * 最近的有效期证书
     */
    private long lastCertValidTime;


    /**
     * 备注
     */
    private String remark;


}
