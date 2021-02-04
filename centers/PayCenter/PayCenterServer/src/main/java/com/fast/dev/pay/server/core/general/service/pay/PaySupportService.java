package com.fast.dev.pay.server.core.general.service.pay;

import com.fast.dev.pay.client.model.PreOrderModel;
import com.fast.dev.pay.client.model.PrePayOrderModel;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.type.PayMethod;

public interface PaySupportService {


    /**
     * 支持的支付方式
     *
     * @return
     */
    PayMethod payMethod();


    /**
     * 执行任务
     *
     * @param payOrder
     * @return
     */
    ResultContent<PreOrderModel> execute(PrePayOrderModel payOrder);


    /**
     * 创建订单
     *
     * @return
     */
    String createOrderId(String serviceCode);


}
