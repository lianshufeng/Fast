package com.fast.dev.pay.server.core.cpcn.param.response;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import com.fast.dev.pay.server.core.cpcn.util.XmlUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.w3c.dom.Document;

@Data
public class RedirectPaymentResponse extends BaseResponse {

    //机构编号
    private String institutionID;
    //支付交易流水号
    private String paymentNo;
    //获取授权码：
    //10-授权码获取成功
    //20-授权码获取失败
    private String status;
    //授权码
    private String authCode;
    //分账模式：
    //10=不分帐
    //20=按比例分账
    //30=按金额分账
    private String splitType;
    //分账结果：
    private String splitResult;
    //平台订单号
    private String traceNo;
    //失效时间
    private String expireTime;
    //支付平台收到银行通知时间
    private String bankTxTime;
    //响应码
    private String responseCode;
    //响应消息
    private String responseMessage;


    public RedirectPaymentResponse(String responseMessage, String responseSignature,  Key key){
        super(responseMessage, responseSignature,key);
    }

    @Override
    @SneakyThrows
    protected void process(Document document) {

        if ("2000".equals(super.code)) {
            this.institutionID = XmlUtil.getNodeText(document, "InstitutionID");
            this.paymentNo = XmlUtil.getNodeText(document, "PaymentNo");
            this.status = XmlUtil.getNodeText(document, "Status");
            this.authCode = XmlUtil.getNodeText(document, "AuthCode");
            this.splitType = XmlUtil.getNodeText(document, "SplitType");
            this.splitResult = XmlUtil.getNodeText(document, "SplitResult");
            this.traceNo = XmlUtil.getNodeText(document, "TraceNo");
            this.expireTime = XmlUtil.getNodeText(document, "ExpireTime");
            this.bankTxTime = XmlUtil.getNodeText(document, "BankTxTime");
            this.responseCode = XmlUtil.getNodeText(document, "ResponseCode");
            this.responseMessage = XmlUtil.getNodeText(document, "ResponseMessage");
        }

    }
}
