package com.fast.dev.pay.client.model;

import com.fast.dev.pay.client.type.PayState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 回调结果集
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallBackResult {

    /**
     * 平台的订单id
     */
    private String orderId;


    /**
     * 结果状态
     */
    private PayState state;

    /**
     * 状态原因
     */
    private String error;


    /**
     * 构建
     *
     * @param state
     * @param orderId
     * @return
     */
    public static CallBackResult build(PayState state, String orderId) {
        CallBackResult callBackResult = new CallBackResult();
        callBackResult.setOrderId(orderId);
        callBackResult.setState(state);
        return callBackResult;
    }

    /**
     * 构建
     *
     * @param state
     * @return
     */
    public static CallBackResult build(PayState state) {
        return build(state, null);
    }

}
