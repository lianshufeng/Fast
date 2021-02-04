package com.fast.dev.pay.server.core.general.dao;

import com.fast.dev.data.mongo.dao.MongoDao;
import com.fast.dev.pay.server.core.general.dao.extend.PayOrderDaoExtend;
import com.fast.dev.pay.server.core.general.domain.PayOrder;

public interface PayOrderDao extends MongoDao<PayOrder>, PayOrderDaoExtend {


    /**
     * 查询支付订单
     *
     * @param orderId
     * @return
     */
    PayOrder findByOrderId(String orderId);


    /**
     * 删除id
     *
     * @param id
     * @return
     */
    long removeById(String id);


}
