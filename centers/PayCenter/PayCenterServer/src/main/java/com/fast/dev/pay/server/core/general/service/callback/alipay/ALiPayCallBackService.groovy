package com.fast.dev.pay.server.core.general.service.callback.alipay

import com.fast.dev.core.util.bean.BeanUtil
import com.fast.dev.pay.client.model.CallBackResult
import com.fast.dev.pay.client.type.AccountType
import com.fast.dev.pay.client.type.PayState
import com.fast.dev.pay.server.core.general.domain.PayOrder
import com.fast.dev.pay.server.core.general.helper.ali.AliPayHelper
import com.fast.dev.pay.server.core.general.helper.weixin.WeiXinHelper
import com.fast.dev.pay.server.core.general.service.callback.SuperCallBackService
import com.netflix.discovery.converters.Auto
import groovy.util.logging.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * 支付宝的回调业务
 */
@Log
@Service
class ALiPayCallBackService extends SuperCallBackService {


    @Override
    AccountType accountType() {
        return AccountType.AliPay;
    }

    @Override
    CallBackResult callback(PayOrder prePayOrder, Object item) {
        super.userLogHelper.log("sourceText", item)
        log.info("trade_status  -> " + item['trade_status'])
        String orderId = item['out_trade_no']

        // 用户扫描二维码触发
        if (item['trade_status'] == 'WAIT_BUYER_PAY') {
            wait_buyer_pay(prePayOrder, item);
            return CallBackResult.build(PayState.Cancel, orderId)
        }


        //支付状态
        PayState state = getPayState(prePayOrder, item)
        return super.finishOrder(orderId, state, BeanUtil.toMap(item));
    }

    @Override
    PayState getPayState(PayOrder prePayOrder, Object item) {
        return item['trade_status'] == 'TRADE_SUCCESS' ? PayState.Paid : PayState.PrePay;
    }
/**
 * 处理等待用户支付的操作
 */
    private void wait_buyer_pay(PayOrder prePayOrder, Object item) {
        //如果订单已被标识关闭，则关闭订单
        if (prePayOrder.getCloseCount() > 0) {
            //微信支付助手
            final AliPayHelper aliPayHelper = this.paySupportHelperCacheManager.get(prePayOrder.getEnterprisePayAccount(), AliPayHelper.class);
            com.fast.dev.pay.client.model.ResponseCloseOrderModel responseCloseOrderModel = aliPayHelper.closeOrder(prePayOrder)
            log.info("close order : " + responseCloseOrderModel.getOther())
        }

    }


}
