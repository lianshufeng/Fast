package com.fast.dev.pay.server.core.general.service.callback.cncp

import com.fast.dev.pay.client.model.CallBackResult
import com.fast.dev.pay.client.type.AccountType
import com.fast.dev.pay.client.type.PayState
import com.fast.dev.pay.server.core.general.dao.PayOrderDao
import com.fast.dev.pay.server.core.general.domain.PayOrder
import com.fast.dev.pay.server.core.general.service.callback.SuperCallBackService
import groovy.util.logging.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Log
@Service
class CNCPCallBackService extends SuperCallBackService {

    @Autowired
    private PayOrderDao payOrderDao

    @Override
    AccountType accountType() {
        return AccountType.CPCNPay
    }

    @Override
    CallBackResult callback(PayOrder payOrder, Object item) {
        super.userLogHelper.log("sourceText", item)
        if (item['status'] == 20) {
            return super.finishOrder(payOrder.getOrderId(), PayState.Paid, item)
        }
        if (item['status'] == 10){
            return super.finishOrder(payOrder.getOrderId(), PayState.PrePay, item)
        }
        CallBackResult callBackResult = super.finishOrder(payOrder.getOrderId(), PayState.Fail, item);
        callBackResult.setError(item['responseMessage'])
        return callBackResult;

//        if (item['status'] == 30){
//            return super.finishOrder(payOrder.getOrderId(),PayState.Fail,item)
//        }
//        return super.finishOrder(payOrder.getOrderId(),PayState.PrePay,item)
    }

    @Override
    PayState getPayState(PayOrder payOrder, Object item) {
        return null
    }
}
