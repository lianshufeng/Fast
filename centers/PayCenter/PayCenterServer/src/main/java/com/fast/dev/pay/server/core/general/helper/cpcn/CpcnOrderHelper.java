package com.fast.dev.pay.server.core.general.helper.cpcn;

import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.pay.client.model.PrePayOrderModel;
import com.fast.dev.pay.client.model.RefundOrderModel;
import com.fast.dev.pay.client.model.ResponseCloseOrderModel;
import com.fast.dev.pay.client.model.ResponseRefundOrderModel;
import com.fast.dev.pay.client.model.account.CPCNAccountModel;
import com.fast.dev.pay.client.model.userbank.UserBankBindCardModel;
import com.fast.dev.pay.client.support.cpcn.CPCNOrder;
import com.fast.dev.pay.client.type.AccountType;
import com.fast.dev.pay.client.util.OrderUtil;
import com.fast.dev.pay.server.core.cpcn.conf.CPCNConf;
import com.fast.dev.pay.server.core.cpcn.param.Key;
import com.fast.dev.pay.server.core.cpcn.param.PayModel;
import com.fast.dev.pay.server.core.cpcn.param.request.*;
import com.fast.dev.pay.server.core.cpcn.param.response.*;
import com.fast.dev.pay.server.core.cpcn.service.CPCNApiService;
import com.fast.dev.pay.server.core.cpcn.util.GUIDGenerator;
import com.fast.dev.pay.server.core.cpcn.util.PFXUtil;
import com.fast.dev.pay.server.core.general.domain.EnterprisePayAccount;
import com.fast.dev.pay.server.core.general.domain.PayOrder;
import com.fast.dev.pay.server.core.general.helper.PaySupportHelper;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
@Getter
@Scope("prototype")
public class CpcnOrderHelper extends PaySupportHelper {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CPCNConf cpcnConf;

    @Autowired
    private CPCNApiService cpcnApiService;

    private String InstitutionID;

    private Key key;


    @Override
    public void after() {
        EnterprisePayAccount enterprisePayAccount = this.getEnterprisePayAccount();
        CPCNAccountModel cpcnAccountModel = enterprisePayAccount.readAccount();
        byte[] pfxBin = Base64.getDecoder().decode(cpcnAccountModel.getPfxCert());
        byte[] pubBin = Base64.getDecoder().decode(cpcnConf.getPubKey());
        PrivateKey privateKey = PFXUtil.getPrivateKey(new ByteArrayInputStream(pfxBin),cpcnAccountModel.getPfxPassWord());
        PublicKey publicKey = PFXUtil.getPublicKey(new ByteArrayInputStream(pubBin));
        this.key = new Key();
        this.key.setPublicKey(publicKey);
        this.key.setPrivateKey(privateKey);
        this.InstitutionID = cpcnAccountModel.getInstitutionID();

    }

    @Override
    public <T> T readObjectFromRequest(HttpServletRequest request, Class<? extends T> cls) {
        return null;
    }

    @Override
    @SneakyThrows
    public Map<String, Object> queryOrder(PayOrder payOrder) {
        QueryRedirectPaymentRequest request = new QueryRedirectPaymentRequest();
        request.setInstitutionID(this.InstitutionID);
        request.setPaymentNo(payOrder.getOrderId());
        request.process(key);
        QueryRedirectPaymentResponse response = (QueryRedirectPaymentResponse) cpcnApiService.queryRedirectPayment(request,key);
        return BeanUtil.toMap(response);
    }

    @Override
    public ResponseCloseOrderModel closeOrder(PayOrder payOrder) {
        RedirectCloseOrderRequest request = new RedirectCloseOrderRequest();
        request.setInstitutionID(this.InstitutionID);
        request.setPaymentNo(payOrder.getOrderId());
        request.setCloseType("10");
        request.setCloseNo(OrderUtil.build(AccountType.CPCNOrderPay,"co"));
        request.process(key);
        RedirectCloseOrderResponse response = (RedirectCloseOrderResponse) cpcnApiService.redirectCloseOrder(request,key);
        ResponseCloseOrderModel model = new ResponseCloseOrderModel();
        model.setTradeNo(payOrder.getOrderId());
        model.setSuccess("10".equals(response.getStatus()));
        model.setOther(BeanUtil.toMap(response));
        return model;
    }

    @Override
    public ResponseRefundOrderModel refund(PayOrder payOrder, RefundOrderModel refundOrderModel) {
        Object tradeNo = refundOrderModel.getRefundNo();
        String tradeNoText = (tradeNo == null ? null : String.valueOf(tradeNo));
        RedirectRefundRequest request = new RedirectRefundRequest();
        request.setInstitutionID(this.InstitutionID);
        request.setPaymentNo(payOrder.getOrderId());
        request.setAmount(String.valueOf(refundOrderModel.getAmount()));
        request.setRefundNo(tradeNoText);
        request.process(key);
        RedirectRefundResponse response = (RedirectRefundResponse) cpcnApiService.redirectRefund(request,key);
        boolean success = (response.getStatus() != null && "10".equals(response.getStatus()));
        return ResponseRefundOrderModel.builder().success(success).tradeNo(tradeNoText).other(BeanUtil.bean2Map(response)).build();
    }

    public RedirectPaymentResponse redirectPayment(PrePayOrderModel payOrder){
        CPCNOrder cpcnOrder = (CPCNOrder) payOrder.getBasePaySupport();
        RedirectPaymentRequest request = new RedirectPaymentRequest();
        request.setInstitutionID(this.InstitutionID);
        request.setPaymentNo(payOrder.getOrderId());
        request.setRedirectSource(cpcnOrder.getRedirectSource().getCode());
        request.setPayWay(cpcnOrder.getPayWay().getCode());
        request.setPayType(cpcnOrder.getPayType().getCode());
        request.setAmount(String.valueOf(payOrder.getPrice()));
        request.setLimitPay("10");
        request.setSettlementFlag(cpcnOrder.getSplitResult());
        request.setSplitType(cpcnOrder.getSplitResult().indexOf("_") > -1 ? "30" : "10");
        request.setPlatformName(cpcnOrder.getPlatformName());
        request.setGoodsName(payOrder.getProduct().getTitle());
/*        request.setPlatformName("31");
        request.setGoodsName("10");*/
        request.setClientIP(cpcnOrder.getClientIP());
        request.setAppID(cpcnOrder.getAppID());
        request.setOpenID(cpcnOrder.getOpenID());
        request.setExpirePeriod("120");
        request.process(key);
        RedirectPaymentResponse response = (RedirectPaymentResponse) cpcnApiService.redirectPayment(request, key);
        return response;
    }

    @Override
    public String sign(String[] parm) {
        return null;
    }




}
