package com.fast.dev.pay.server.core.hb.model.resp;

import com.fast.dev.pay.server.core.hb.type.Status;
import lombok.Data;

@Data
public class QueryTransResultResp extends HBResp {

    /**
     * 原交易返回码
     */
    private String orgRetCode;

    /**
     * 原交易返回信息
     */
    private String orgRetMsg;

    /**
     * 原交易状态
     */
    private String orgStatus;
}
