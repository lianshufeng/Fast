package com.fast.dev.pay.client.support.cpcn;

import com.fast.dev.pay.client.support.BasePaySupport;
import lombok.Data;

/**
 * 中金支付订单
 */
@Data
public class CPCNPayOrder extends BasePaySupport {

    //用户银行卡的绑卡ID
    private String userCardId;

    //分账结果
    private String splitResult;

}


