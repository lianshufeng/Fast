package com.fast.dev.pay.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRefundOrderModel {

    //返回结果
    private boolean success;

    //交易号
    private String tradeNo;


    //消息
    private Map<String, Object> other;


}
