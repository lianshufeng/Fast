package com.fast.dev.pay.server.core.hb.model.resp;

import com.fast.dev.pay.server.core.hb.type.Status;
import lombok.Data;

@Data
public class HBResp {

    /**
     * 交易时间
     */
    private String txnTime;

    /**
     * 返回码
     */
    private String retCode;

    /**
     * 返回信息
     */
    private String retMsg;

    /**
     * 交易日期
     */
    private String txnDate;

    /**
     * 交易流水号
     */
    private String orgTradeNo;

    /**
     * 交易状态
     */
    private Status status;
}
