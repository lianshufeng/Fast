package com.fast.dev.pay.server.core.general.dao.impl;

import com.fast.dev.data.mongo.helper.DBHelper;
import com.fast.dev.pay.server.core.general.dao.PayOrderDao;
import com.fast.dev.pay.server.core.general.dao.extend.CallBackOrderDaoExtend;
import com.fast.dev.pay.server.core.general.domain.CallBackOrder;
import com.fast.dev.pay.server.core.general.domain.PayOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Map;

public class CallBackOrderDaoImpl implements CallBackOrderDaoExtend {

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PayOrderDao payOrderDao;

    @Override
    public String add(String orderId, Map<String, Object> item) {
        CallBackOrder backOrder = new CallBackOrder();
        //允许订单为空
        PayOrder payOrder = this.payOrderDao.findByOrderId(orderId);
        backOrder.setPayOrder(payOrder);
        backOrder.setItem(item);

        this.dbHelper.saveTime(backOrder);
        this.mongoTemplate.save(backOrder);
        return backOrder.getId();
    }
}
