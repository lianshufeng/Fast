package com.fast.dev.pay.client.type;

/**
 * 支付状态
 */
public enum PayState {
    /**
     * 未支付
     */
    PrePay,


    /**
     * 已支付
     */
    Paid,


    /**
     * 失败
     */
    Fail,


    /**
     * 取消
     */
    Cancel
}