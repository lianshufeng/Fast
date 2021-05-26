

package com.fast.dev.pay.server.core.general.domain;

import com.fast.dev.data.mongo.domain.SuperEntity;
import com.fast.dev.pay.client.model.Product;
import com.fast.dev.pay.client.model.RefundModel;
import com.fast.dev.pay.client.type.PayMethod;
import com.fast.dev.pay.client.type.PayState;
import com.fast.dev.pay.client.type.ProductType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 支付订单
 */
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class PayOrder extends SuperEntity {


    /**
     * 业务订单id
     */
    private String serviceOrder;


    /**
     * 订单
     */
    @Indexed(unique = true)
    private String orderId;


    /**
     * 关联的支付账号
     */
    @Indexed
    @DBRef(lazy = true)
    private EnterprisePayAccount enterprisePayAccount;


    /**
     * 用户中心的id
     */
    @Indexed
    private String uid;


    /**
     * 商品描述
     */
    private Product product;


    /**
     * 金额
     */
    @Indexed
    private Long price;


    /**
     * 支付方式
     */
    @Indexed
    private PayMethod method;

    /**
     * 预付款平台反馈信息
     */
    @Indexed
    private Map<String, Object> supportPre;


    /**
     * 三方支付的信息源
     */
    private Map<String, Object> supportPost;

    /**
     * 支付状态
     */
    @Indexed
    private PayState state;


    /**
     * 唯一索引
     */
    @Indexed(unique = true)
    private String uniqueIndex;


    /**
     * 广播次数
     */
    @Indexed
    private long broadCount;


    /**
     * 订单是否已关闭
     */
    @Indexed
    private boolean close;

    /**
     * 预关闭次数
     */
    @Indexed
    private int closeCount;


    /**
     * 退款信息
     */
    private Set<RefundModel> refund;


}
