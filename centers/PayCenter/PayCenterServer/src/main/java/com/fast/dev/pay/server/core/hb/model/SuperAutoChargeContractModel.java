package com.fast.dev.pay.server.core.hb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuperAutoChargeContractModel extends HuaXiaEnterpriseAutoChargeContractModel {
    //短信验证码
    private String messageNo;

    /**
     * 原交易流水号
     */
    private String orgTradeNo;

    /**
     * 原交易日期
     */
    private String txnDate;

    //验证码id
    private String sessionId;

    //套餐模板
    private String templateId;

}