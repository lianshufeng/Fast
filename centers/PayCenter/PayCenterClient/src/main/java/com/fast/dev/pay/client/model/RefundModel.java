package com.fast.dev.pay.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundModel {

    /**
     * 退款的交易号
     */
    private String tradeNo;

    /**
     *
     */
    private Map<String,Object> other;


}
