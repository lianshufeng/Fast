package com.fast.dev.pay.client.model;

import com.fast.dev.pay.client.support.BasePaySupport;
import com.fast.dev.pay.client.type.PayState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 预支付订单
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrePayOrderModel extends CreatePayOrderModel {


    /**
     * 其他参数
     */
    private BasePaySupport basePaySupport;


    /**
     * 支付状态
     */
    private PayState state;


    /**
     * 订单id
     */
    private String orderId;

}
