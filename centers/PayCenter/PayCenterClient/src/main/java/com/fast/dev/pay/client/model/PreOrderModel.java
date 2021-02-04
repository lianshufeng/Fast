package com.fast.dev.pay.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 预付费订单
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreOrderModel {

    //平台的订单
    private String platformOrder;

    //第三方与付费订单
    private Map<String, Object> support;

    //错误信息
    private Map<String, Object> error;
}
