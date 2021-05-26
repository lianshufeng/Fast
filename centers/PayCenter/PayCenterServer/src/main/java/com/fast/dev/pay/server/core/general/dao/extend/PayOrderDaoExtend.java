package com.fast.dev.pay.server.core.general.dao.extend;

import com.fast.dev.pay.client.model.PrePayOrderModel;
import com.fast.dev.pay.client.type.PayState;
import com.fast.dev.pay.server.core.general.domain.PayOrder;

import java.util.Map;

public interface PayOrderDaoExtend {


    /**
     * 订单是否已存在
     *
     * @param payOrderModel
     * @return
     */
    boolean existOrder(PrePayOrderModel payOrderModel);


    /**
     * 通过预创建订单查询对象
     *
     * @param payOrderModel
     * @return
     */
    PayOrder findByPreOrder(PrePayOrderModel payOrderModel);


    /**
     * 创建订单
     *
     * @return
     */
    PayOrder createOrder(PrePayOrderModel payOrderModel);


    /**
     * 设置预付费订单号
     *
     * @param orderId
     * @param supportPre
     */
    boolean setSupportPre(String orderId, Map<String, Object> supportPre);


    /**
     * 设置订单状态
     *
     * @param orderId
     * @return
     */
    boolean setOrderState(String orderId, PayState state);


    /**
     * 设置定状态和三方数据源
     *
     * @param orderId
     * @param state
     * @param supportPost
     * @return
     */
    boolean setOrderStateAndSupportPost(String orderId, PayState state, Map<String, Object> supportPost);


    /**
     * 找到订单并更新广播次数
     *
     * @param orderId
     * @return
     */
    PayOrder findPayOrderAndIncBroadCount(String orderId);


    /**
     * @param orderId
     * @param tradeNo
     * @param info
     * @return
     */
    boolean updateRefund(String orderId, String tradeNo, Object info,long amount);


    /**
     * 关闭订单
     *
     * @param orderId
     * @return
     */
    boolean closeOrder(String orderId);


}
