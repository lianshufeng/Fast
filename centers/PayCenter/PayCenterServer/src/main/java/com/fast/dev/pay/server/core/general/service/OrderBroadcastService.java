package com.fast.dev.pay.server.core.general.service;

import com.fast.dev.pay.client.model.PayCallBackModel;
import com.fast.dev.pay.client.model.PayOrderModel;
import com.fast.dev.pay.server.core.general.dao.PayOrderDao;
import com.fast.dev.pay.server.core.general.domain.PayOrder;
import com.fast.dev.pay.server.core.general.helper.PayCenterCallBackHelper;
import com.fast.dev.pay.server.core.general.service.impl.PayServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderBroadcastService {

    @Autowired
    private PayOrderDao payOrderDao;

    @Autowired
    private PayServiceImpl payService;

    @Autowired
    private PayCenterCallBackHelper payCenterCallBackHelper;


    /**
     * 订单发广播
     */
    public void publish(String orderId) {
        //构建回调模型
        PayCallBackModel payCallBackModel = new PayCallBackModel();

        PayOrder payOrder = this.payOrderDao.findPayOrderAndIncBroadCount(orderId);
        PayOrderModel payOrderModel = this.payService.toModel(payOrder);
        BeanUtils.copyProperties(payOrderModel, payCallBackModel);

        this.payCenterCallBackHelper.publish(payCallBackModel);
    }
}
