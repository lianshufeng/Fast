package com.fast.dev.pay.server.core.general.service.pay;

import com.fast.dev.pay.client.model.PrePayOrderModel;
import com.fast.dev.pay.client.model.PreOrderModel;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.result.ResultState;
import com.fast.dev.pay.client.type.PayMethod;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Log
@Service
public class PaySupportServiceManager {

    @Autowired
    private ApplicationContext applicationContext;


    private Map<PayMethod, PaySupportService> paySupportServiceCache = new ConcurrentHashMap<>();


    @Autowired
    private void init() {
        paySupportServiceCache.clear();
        applicationContext.getBeansOfType(PaySupportService.class).values().forEach((it) -> {
            paySupportServiceCache.put(it.payMethod(), it);
        });
    }


    /**
     * 执行任务
     *
     * @param payOrder
     * @return
     */
    public ResultContent<PreOrderModel> execute(PrePayOrderModel payOrder) {
        PaySupportService paySupportService = this.paySupportServiceCache.get(payOrder.getMethod());
        if (paySupportService == null) {
            return ResultContent.build(ResultState.NotPaySupport);
        }
        return paySupportService.execute(payOrder);
    }


    /**
     * 获取第三方支付的业务
     *
     * @param payMethod
     * @return
     */
    public Optional<PaySupportService> getPaySupportService(PayMethod payMethod) {
        return Optional.ofNullable(this.paySupportServiceCache.get(payMethod));
    }


}
