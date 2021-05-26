package com.fast.dev.pay.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundModel {

    /**
     * 退款的交易号
     */
    private String tradeNo;

    /**
     * 其他信息
     */
    private Map<String,Object> other;

    /**
     * 退款金额
     */
    private long amount;


}
