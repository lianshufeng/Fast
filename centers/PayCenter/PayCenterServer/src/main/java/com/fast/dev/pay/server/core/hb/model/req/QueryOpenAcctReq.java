package com.fast.dev.pay.server.core.hb.model.req;

import com.fast.dev.pay.server.core.hb.type.BizType;
import lombok.Data;

@Data
public class QueryOpenAcctReq extends BaseRequest {

    /**
     * 原交易业务类型
     */
    private BizType bizType;

    /**
     * 原交易流水号
     */
    private String orgTradeNo;

    /**
     * 原交易日期
     */
    private String orgPcsDate;




}
