package com.fast.dev.pay.server.core.general.service.pay.impl.cpcn;

import com.fast.dev.auth.client.log.helper.UserLogHelper;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.pay.client.model.PreOrderModel;
import com.fast.dev.pay.client.model.PrePayOrderModel;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.result.ResultState;
import com.fast.dev.pay.client.support.cpcn.CPCNOrder;
import com.fast.dev.pay.client.type.AccountType;
import com.fast.dev.pay.client.type.PayMethod;
import com.fast.dev.pay.client.util.OrderUtil;
import com.fast.dev.pay.server.core.cpcn.param.response.RedirectPaymentResponse;
import com.fast.dev.pay.server.core.general.dao.EnterprisePayAccountDao;
import com.fast.dev.pay.server.core.general.domain.EnterprisePayAccount;
import com.fast.dev.pay.server.core.general.helper.PaySupportHelperCacheManager;
import com.fast.dev.pay.server.core.general.helper.cpcn.CpcnHelper;
import com.fast.dev.pay.server.core.general.helper.cpcn.CpcnOrderHelper;
import com.fast.dev.pay.server.core.general.service.pay.PaySupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 中金银行的跳转支付
 */
@Service
public class CPCNOrderService implements PaySupportService {


    @Autowired
    private EnterprisePayAccountDao enterprisePayAccountDao;

    @Autowired
    private PaySupportHelperCacheManager paySupportHelperCacheManager;

    @Autowired
    private UserLogHelper userLogHelper;



    @Override
    public PayMethod payMethod() {
        return PayMethod.CPCNOrderPay;
    }

    @Override
    public ResultContent<PreOrderModel> execute(PrePayOrderModel payOrder) {




        EnterprisePayAccount enterprisePayAccount = this.enterprisePayAccountDao.findTop1ById(payOrder.getPayAccountId());

        final CpcnOrderHelper cpcnOrderHelper = paySupportHelperCacheManager.get(enterprisePayAccount, CpcnOrderHelper.class);

        RedirectPaymentResponse response = cpcnOrderHelper.redirectPayment(payOrder);
        userLogHelper.log("response", JsonUtil.toJson(response));
        PreOrderModel preOrderModel = null;
        if (response.getStatus() != null && response.getStatus().equals("10")){
            preOrderModel = PreOrderModel.builder().platformOrder(payOrder.getOrderId()).support(BeanUtil.bean2Map(response)).build();
            return ResultContent.build(ResultState.Success,preOrderModel);
        } else {
            preOrderModel = PreOrderModel.builder().platformOrder(payOrder.getOrderId()).error(BeanUtil.bean2Map(response)).build();
            return ResultContent.build(ResultState.Fail,preOrderModel);
        }
    }

    @Override
    public String createOrderId(String serviceCode) {
        return OrderUtil.build(AccountType.CPCNOrderPay,serviceCode,9);
    }
}
