package com.fast.dev.pay.server.core.general.service.callback;

import com.fast.dev.auth.client.log.helper.UserLogHelper;
import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.pay.client.model.CallBackResult;
import com.fast.dev.pay.client.result.ResultState;
import com.fast.dev.pay.client.type.AccountType;
import com.fast.dev.pay.client.type.PayState;
import com.fast.dev.pay.server.core.general.dao.CallBackOrderDao;
import com.fast.dev.pay.server.core.general.dao.PayOrderDao;
import com.fast.dev.pay.server.core.general.domain.PayOrder;
import com.fast.dev.pay.server.core.general.helper.PayCenterCallBackHelper;
import com.fast.dev.pay.server.core.general.helper.PaySupportHelperCacheManager;
import com.fast.dev.pay.server.core.general.service.OrderBroadcastService;
import com.fast.dev.pay.server.core.general.service.impl.PayServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public abstract class SuperCallBackService {

    @Autowired
    private OrderBroadcastService orderBroadcastService;

    @Autowired
    protected CallBackOrderDao callBackOrderDao;

    @Autowired
    protected PayOrderDao payOrderDao;

    @Autowired
    protected PayServiceImpl payService;


    @Autowired
    protected PayCenterCallBackHelper payCenterCallBackHelper;


    @Autowired
    protected PaySupportHelperCacheManager paySupportHelperCacheManager;


    @Autowired
    protected UserLogHelper userLogHelper;


    /**
     * 回调处理的账户类型
     *
     * @return
     */
    public abstract AccountType accountType();


    /**
     * 回调方法
     *
     * @param prePayOrder 数据库订单
     * @param item        解密或签名验证过的数据对象
     * @return
     */
    public abstract CallBackResult callback(PayOrder prePayOrder, Object item);


    /**
     * 是否成功
     *
     * @param prePayOrder
     * @param item
     * @return
     */
    public abstract PayState getPayState(PayOrder prePayOrder, Object item);


    /**
     * 完成订单
     *
     * @param orderId
     */
//    @Transactional
    protected CallBackResult finishOrder(String orderId, PayState state, Map<String, Object> data) {

        //平台支付订单
        final PayOrder payOrder = this.payOrderDao.findByOrderId(orderId);

        if (payOrder == null) {
            return CallBackResult.build(PayState.Fail);
        }


        //更新数据库状态以及数据源
        this.payOrderDao.setOrderStateAndSupportPost(orderId, state, BeanUtil.toMap(data));


        //修改订单为支付成功
        CallBackResult result = new CallBackResult(orderId, state);


        //如果业务处理成功则进行广播
        if (result.getState() == PayState.Paid) {
            this.orderBroadcastService.publish(orderId);
        }


        return result;

    }


}
