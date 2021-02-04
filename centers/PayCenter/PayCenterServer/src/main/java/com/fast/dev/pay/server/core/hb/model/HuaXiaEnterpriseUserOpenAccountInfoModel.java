package com.fast.dev.pay.server.core.hb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 华夏企业账户开户流水
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaEnterpriseUserOpenAccountInfoModel {


    /**
     * 虚拟卡号
     */
    private String cardNo;

    /**
     * 开户行号
     */
    private String accountNodeNo;

    /**
     * 客户号
     */
    private String custNo;

    /**
     * 交易日期
     */
    private String txnDate;

    /**
     * 交易流水号
     */
    private String orgTradeNo;

    /**
     * 用户名
     */
    private String userName;

}
