package com.fast.dev.pay.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单退款模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundOrderModel {

    //订单号
    private String orderId;

    //退款号
    private String refundNo;


    //退款原因
    private String reason;


    //退款金额
    private long amount;


}
