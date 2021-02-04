package com.fast.dev.pay.server.core.hb.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.data.timer.domain.SimpleTaskTimerTable;
import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseCertModel;
import com.fast.dev.pay.server.core.hb.model.TaskProcessModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 华夏银行的企业账户
 */
@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaEnterpriseAccount extends SimpleTaskTimerTable {

    //企业账户，如果该字段为空，则表明为成为正式企业
    @Indexed(unique = true, sparse = true)
    private String epId;

    //编码
    @Indexed(unique = true, sparse = true)
    private String code;

    //企业名
    @Indexed
    private String enterpriseName;

    //企业负责人手机
    @Indexed
    private String enterprisePhone;

    //企业负责人
    @Indexed
    private String enterprisePersonName;

    //商户id
    @Indexed
    private String mchtId;

    //商户号
    @Indexed
    private String mchtNo;

    //应用id
    @Indexed
    private String appid;

    //企业证书
    @Indexed
    private HuaXiaEnterpriseCertModel certModel;

    //工作时间，单位分钟，如早上8点为 : 8 * 60， 意见时间为早上8点到晚上20点之前
    @Indexed
    private Integer workTime;

    //销售的手机号码
    @Indexed
    private String salePhone;

    //工作任务
    @Indexed
    private TaskProcessModel taskProcess;

    //收款客户电话
    @Indexed
    private String customerPhone;

}
