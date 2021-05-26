package com.fast.dev.pay.client.model.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 中金支付的模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPCNAccountModel extends SuperAccount {

    //pfx的证书文件(base64编码)
    private String pfxCert;

    //pfx证书的密码
    private String pfxPassWord;

    //机构编号
    private String institutionID;

    //分账标识
    private Map<String,String> splitFlag;



}
