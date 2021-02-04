package com.fast.dev.pay.client.model;

import com.fast.dev.pay.client.type.PayMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 创建支付订单模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePayOrderModel {

    /**
     * 订单id，要求业务不重复
     */
    private String serviceOrder;

    /**
     * 业务编码,2位长度
     */
    private String serviceCode;


    /**
     * 支付账号
     */
    private String payAccountId;


    /**
     * 用户中心的id
     */
    private String uid;


    /**
     * 商品描述
     */
    private Product product;


    /**
     * 金额,单位分
     */
    private Long price;


    /**
     * 支付方式
     */
    private PayMethod method;

    /**
     * 创建订单的扩展参数
     */
    private Map<String, Object> parm;


}
