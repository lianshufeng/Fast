package com.fast.dev.pay.server.core.hb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class HuaXiaEnterpriseAccountModel {


    //平台的企业id
    private String epId;

    //华夏企业账户id
    private String id;

    //企业编码
    private String code;

    //企业名
    private String enterpriseName;

    //企业负责人手机
    private String enterprisePhone;

    //企业负责人
    private String enterprisePersonName;

    //商户id
    private String mchtId;

    //商户号
    @Indexed
    private String mchtNo;

    //应用id
    private String appid;

    //企业证书
    private HuaXiaEnterpriseCertModel certModel;

    //工作时间，单位分钟，如早上8点为 : 8 * 60， 意见时间为早上8点到晚上20点之前
    private Integer workTime;

    //销售手机号码
    private String salePhone;


    //收款客户电话
    private String customerPhone;
}
