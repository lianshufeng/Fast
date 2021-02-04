package com.fast.dev.pay.server.core.hb.model.resp;

import com.fast.dev.pay.server.core.hb.type.Status;
import lombok.Data;

@Data
public class FreezeAcctResp extends HBResp {



    /**
     * 可解冻金额
     */
    private String avlFrzAmt;

    /**
     * 解冻处理中金额
     */
    private String inFrzAmt;

}