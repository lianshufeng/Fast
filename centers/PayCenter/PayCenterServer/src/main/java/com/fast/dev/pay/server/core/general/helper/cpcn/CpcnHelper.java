package com.fast.dev.pay.server.core.general.helper.cpcn;

import com.fast.dev.auth.client.log.helper.UserLogHelper;
import com.fast.dev.core.util.JsonUtil;
import com.fast.dev.core.util.bean.BeanUtil;
import com.fast.dev.pay.client.model.RefundOrderModel;
import com.fast.dev.pay.client.model.ResponseCloseOrderModel;
import com.fast.dev.pay.client.model.ResponseRefundOrderModel;
import com.fast.dev.pay.client.model.account.CPCNAccountModel;
import com.fast.dev.pay.client.model.userbank.UserBankBindCardModel;
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
public class CpcnHelper extends PaySupportHelper {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private CPCNConf cpcnConf;

    @Autowired
    private CPCNApiService cpcnApiService;


    private String InstitutionID;

    private Key key;

    @Autowired
    private UserLogHelper userLogHelper;


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
    public Map<String, Object> queryOrder(PayOrder payOrder) {
        QueryPaymentResultRequest request = new QueryPaymentResultRequest();
        request.setInstitutionID(this.InstitutionID);
        request.setPaymentNo(payOrder.getOrderId());
        request.process(key);
        QueryPaymentResultResponse response = (QueryPaymentResultResponse) cpcnApiService.queryPaymentResult(request,key);
        return BeanUtil.bean2Map(response);
    }

    @Override
    public ResponseCloseOrderModel closeOrder(PayOrder payOrder) {
        return null;
    }

    @Override
    public ResponseRefundOrderModel refund(PayOrder payOrder, RefundOrderModel refundOrderModel) {
        Object tradeNo = refundOrderModel.getRefundNo();
        String tradeNoText = (tradeNo == null ? null : String.valueOf(tradeNo));
        RefundRequest request = new RefundRequest();
        request.setInstitutionID(this.InstitutionID);
        request.setPaymentNo(payOrder.getOrderId());
        request.setTxSN(tradeNoText);
        request.setAmount(refundOrderModel.getAmount());
        request.process(key);
        RefundResponse response = (RefundResponse) cpcnApiService.refund(request,key);
        boolean success = (response.getCode() != null && "2000".equals(response.getCode()));
        return ResponseRefundOrderModel.builder().success(success).tradeNo(tradeNoText).other(BeanUtil.bean2Map(response)).build();
    }

    @Override
    public String sign(String[] parm) {
        return null;
    }

    public Map<String, Object> payment(PayOrder order, PayModel payModel){
        PaymentRequest request = new PaymentRequest();
        request.setInstitutionID(this.InstitutionID);
        request.setPaymentNo(order.getOrderId());
        request.setAmount(order.getPrice());
        request.setTxSNBinding(payModel.getTxSNBinding());
        request.setSettlementFlag(payModel.getSplitResult());
        request.setRemark(order.getProduct().getRemark());
        request.setSplitType(payModel.getSplitResult().indexOf("_") > -1 ? "30" : "10");
        request.process(key);
        PaymentResponse response = (PaymentResponse) cpcnApiService.payment(request,key);
        userLogHelper.log("response", JsonUtil.toJson(response));
        return BeanUtil.bean2Map(response);
    }

    public PreBindCardResponse preBindCard(UserBankBindCardModel userBankBindCard){
        PreBindCardRequest request = new PreBindCardRequest();
        request.setInstitutionID(this.InstitutionID);
        request.setTxSNBinding(userBankBindCard.getTxSNBinding());
        request.setBankID((String) cpcnApiService.getBankCode(userBankBindCard.getAccountNumber()));
        request.setAccountName(userBankBindCard.getAccountName());
        request.setAccountNumber(userBankBindCard.getAccountNumber());
        request.setIdentificationNumber(userBankBindCard.getIdentificationNumber());
        request.setIdentificationType("0");
        request.setPhoneNumber(userBankBindCard.getPhoneNumber());
        request.setCardType("10");
        request.process(key);
        PreBindCardResponse response = (PreBindCardResponse) cpcnApiService.preBindCard(request,key);
        return response;
    }

    public QueryBindCardResponse queryBindCard(UserBankBindCardModel userBankBindCard){
        QueryBindCardRequest request = new QueryBindCardRequest();
        request.setInstitutionID(this.InstitutionID);
        request.setAccountNumber(userBankBindCard.getAccountNumber());
        request.process(key);
        QueryBindCardResponse response = (QueryBindCardResponse) cpcnApiService.queryBindCard(request,key);
        return response;
    }

    public ConfirmBindCardResponse confirmBindCard(String txSNBinding,String code){
        ConfirmBindCardRequest request = new ConfirmBindCardRequest();
        request.setInstitutionID(this.InstitutionID);
        request.setTxSNBinding(txSNBinding);
        request.setSMSValidationCode(code);
        request.process(key);
        ConfirmBindCardResponse response = (ConfirmBindCardResponse) cpcnApiService.confirmBindCard(request,key);
        return response;
    }

    public CancelBindCardResponse cancelBindCard(String txSNBinding){
        CancelBindCardRequest request = new CancelBindCardRequest();
        request.setInstitutionID(this.InstitutionID);
        request.setTxSNUnBinding(GUIDGenerator.genGUID());
        request.setTxSNBinding(txSNBinding);
        request.setUnBindingType("1");
        request.process(key);
        CancelBindCardResponse response = (CancelBindCardResponse) cpcnApiService.cancelBindCard(request,key);
        return response;

    }

    public DirectBindCardResponse directBindCard(UserBankBindCardModel userBankBindCard){
        DirectBindCardRequest request = new DirectBindCardRequest();
        request.setInstitutionID(this.InstitutionID);
        request.setTxSNBinding(userBankBindCard.getTxSNBinding());
        request.setBankID((String) cpcnApiService.getBankCode(userBankBindCard.getAccountNumber()));
        request.setAccountName(userBankBindCard.getAccountName());
        request.setAccountNumber(userBankBindCard.getAccountNumber());
        request.setIdentificationNumber(userBankBindCard.getIdentificationNumber());
        request.setIdentificationType("0");
        request.setPhoneNumber(userBankBindCard.getPhoneNumber());
        request.setCardType("10");
        request.process(key);
        DirectBindCardResponse response  = (DirectBindCardResponse) cpcnApiService.directBindCard(request,key);
        return response;
    }


}
