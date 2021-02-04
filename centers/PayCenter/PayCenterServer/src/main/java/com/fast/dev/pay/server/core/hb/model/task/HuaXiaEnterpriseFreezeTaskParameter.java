package com.fast.dev.pay.server.core.hb.model.task;

import com.fast.dev.pay.server.core.hb.model.HuaXiaEnterpriseTaskParameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuaXiaEnterpriseFreezeTaskParameter extends HuaXiaEnterpriseTaskParameter {

    //冻结总金额
    private long amount;

    /**
     * 交易日期
     */
    private String txnDate;

    /**
     * 交易流水号
     */
    private String orgTradeNo;


}
