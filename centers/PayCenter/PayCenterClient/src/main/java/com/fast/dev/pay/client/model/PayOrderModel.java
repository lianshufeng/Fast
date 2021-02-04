package com.fast.dev.pay.client.model;

import com.fast.dev.pay.client.type.PayMethod;
import com.fast.dev.pay.client.type.PayState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayOrderModel {

    /**
     * 业务订单id
     */
    private String serviceOrder;


    /**
     * 企业支付账号id
     */
    private String payAccountId;


    /**
     * 订单
     */
    private String orderId;


    /**
     * 用户中心的id
     */
    private String uid;


    /**
     * 商品描述
     */
    private Product product;


    /**
     * 金额
     */
    private Long price;


    /**
     * 支付方式
     */
    private PayMethod method;

    /**
     * 预付款平台反馈信息
     */
    private Map<String, Object> supportPre;


    /**
     * 三方支付的信息源
     */
    private Map<String, Object> supportPost;


    /**
     * 支付状态
     */
    private PayState state;


    /**
     * 唯一索引
     */
    private String uniqueIndex;


    /**
     * 广播次数
     */
    private long broadCount;


    /**
     * 退款信息
     */
    private Set<RefundModel> refund;


    /**
     * 订单是否关闭
     */
    private boolean close;

}
