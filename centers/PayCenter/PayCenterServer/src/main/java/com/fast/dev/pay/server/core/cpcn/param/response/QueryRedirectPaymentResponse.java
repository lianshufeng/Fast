package com.fast.dev.pay.server.core.cpcn.param.response;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import com.fast.dev.pay.server.core.cpcn.util.XmlUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.w3c.dom.Document;

@Data
public class QueryRedirectPaymentResponse extends BaseResponse {


    private String institutionID;
    private String paymentNo;
    private String status;
    private String splitType;
    private String splitResult;
    private String crdType;
    private String amount;
    private String balance;
    private String couponAmount;
    private String bankTxTime;
    private String openID;
    private String responseCode;
    private String responseMessage;
    private String remark;

    public QueryRedirectPaymentResponse(String responseMessage, String responseSignature,  Key key){
        super(responseMessage, responseSignature,key);
    }


    @Override
    @SneakyThrows
    protected void process(Document document) {
        if ("2000".equals(super.code)) {
            this.institutionID = XmlUtil.getNodeText(document, "InstitutionID");
            this.paymentNo = XmlUtil.getNodeText(document, "PaymentNo");
            this.status = XmlUtil.getNodeText(document, "Status");
            this.splitType = XmlUtil.getNodeText(document, "SplitType");
            this.splitResult = XmlUtil.getNodeText(document, "SplitResult");
            this.crdType = XmlUtil.getNodeText(document, "CrdType");
            this.amount = XmlUtil.getNodeText(document, "Amount");
            this.balance = XmlUtil.getNodeText(document, "Balance");
            this.couponAmount = XmlUtil.getNodeText(document, "CouponAmount");
            this.bankTxTime = XmlUtil.getNodeText(document, "BankTxTime");
            this.openID = XmlUtil.getNodeText(document, "OpenID");
            this.responseCode = XmlUtil.getNodeText(document, "ResponseCode");
            this.responseMessage = XmlUtil.getNodeText(document, "ResponseMessage");
            this.remark = XmlUtil.getNodeText(document, "Remark");
        }
    }
}
