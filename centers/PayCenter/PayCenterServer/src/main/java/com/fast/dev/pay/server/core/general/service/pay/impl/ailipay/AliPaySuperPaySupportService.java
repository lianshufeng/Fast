package com.fast.dev.pay.server.core.general.service.pay.impl.ailipay;

import com.fast.dev.auth.client.log.helper.UserLogHelper;
import com.fast.dev.pay.client.model.PreOrderModel;
import com.fast.dev.pay.client.model.PrePayOrderModel;
import com.fast.dev.pay.client.result.ResultContent;
import com.fast.dev.pay.client.type.AccountType;
import com.fast.dev.pay.client.util.OrderUtil;
import com.fast.dev.pay.server.core.general.dao.EnterprisePayAccountDao;
import com.fast.dev.pay.server.core.general.domain.EnterprisePayAccount;
import com.fast.dev.pay.server.core.general.helper.PaySupportHelperCacheManager;
import com.fast.dev.pay.server.core.general.helper.ali.AliPayHelper;
import com.fast.dev.pay.server.core.general.service.pay.PaySupportService;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AliPaySuperPaySupportService implements PaySupportService {

    @Autowired
    private EnterprisePayAccountDao enterprisePayAccountDao;

    @Autowired
    private PaySupportHelperCacheManager paySupportHelperCacheManager;


    @Autowired
    private UserLogHelper logHelper;

    @Override
    public ResultContent<PreOrderModel> execute(PrePayOrderModel payOrder) {
        //企业支付账户
        final EnterprisePayAccount enterprisePayAccount = this.enterprisePayAccountDao.findTop1ById(payOrder.getPayAccountId());
        //支付宝助手
        final AliPayHelper aliPayHelper = paySupportHelperCacheManager.get(enterprisePayAccount, AliPayHelper.class);

        return execute(aliPayHelper, payOrder);
    }


    /**
     * 具体业务层处理
     *
     * @param aliPayHelper
     * @param payOrder
     * @return
     */
    public abstract ResultContent<PreOrderModel> execute(AliPayHelper aliPayHelper, PrePayOrderModel payOrder);


    @Override
    public String createOrderId(String serviceCode) {
        return OrderUtil.build(AccountType.AliPay, serviceCode);
    }


}
