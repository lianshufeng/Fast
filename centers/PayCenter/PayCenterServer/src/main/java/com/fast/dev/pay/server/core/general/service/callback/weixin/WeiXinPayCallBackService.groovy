package com.fast.dev.pay.server.core.general.service.callback.weixin

import com.fast.dev.pay.client.model.CallBackResult
import com.fast.dev.pay.client.type.AccountType
import com.fast.dev.pay.client.type.PayState
import com.fast.dev.pay.server.core.general.domain.PayOrder
import com.fast.dev.pay.server.core.general.service.callback.SuperCallBackService
import groovy.util.logging.Log
import org.springframework.stereotype.Service

@Log
@Service
class WeiXinPayCallBackService extends SuperCallBackService {


    /**
     * 微信的回调业务
     */
    @Override
    AccountType accountType() {
        return AccountType.WeiXinPay;
    }

    @Override
    public CallBackResult callback(PayOrder prePayOrder, Object item) {

        //平台订单
        String orderId = item['out_trade_no'];

        //支付状态
        PayState state = getPayState(prePayOrder, item);

        //完成订单
        return super.finishOrder(orderId, state, item);
    }

    @Override
    PayState getPayState(PayOrder prePayOrder, Object item) {
        return item['trade_state'] == 'SUCCESS' ? PayState.Paid : PayState.Fail;
    }
}
