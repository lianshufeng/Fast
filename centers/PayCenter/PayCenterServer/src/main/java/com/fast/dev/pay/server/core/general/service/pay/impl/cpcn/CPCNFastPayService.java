package com.fast.dev.pay.server.core.general.service.pay.impl.cpcn;

import com.fast.dev.pay.client.model.PreOrderModel;
import com.fast.dev.pay.client.model.PrePayOrderModel;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.type.AccountType;
import com.fast.dev.pay.client.type.PayMethod;
import com.fast.dev.pay.client.util.OrderUtil;
import com.fast.dev.pay.server.core.cpcn.util.GUIDGenerator;
import com.fast.dev.pay.server.core.general.service.pay.PaySupportService;
import org.springframework.stereotype.Service;

@Service
public class CPCNFastPayService implements PaySupportService {
    @Override
    public PayMethod payMethod() {
        return PayMethod.CPCNFastPay;
    }

    @Override
    public ResultContent<PreOrderModel> execute(PrePayOrderModel payOrder) {
        PreOrderModel preOrderModel = new PreOrderModel();
        preOrderModel.setPlatformOrder(payOrder.getOrderId());
        return ResultContent.buildContent(preOrderModel);
    }

    @Override
    public String createOrderId(String serviceCode) {
        //return GUIDGenerator.genGUID();
        return OrderUtil.build(AccountType.CPCNPay,serviceCode,9);
    }
}
