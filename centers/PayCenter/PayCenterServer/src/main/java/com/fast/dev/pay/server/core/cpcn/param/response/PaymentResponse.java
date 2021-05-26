package com.fast.dev.pay.server.core.cpcn.param.response;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import com.fast.dev.pay.server.core.cpcn.util.XmlUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.w3c.dom.Document;

import java.security.PublicKey;

@Data
@NoArgsConstructor
public class PaymentResponse extends BaseResponse{

    //机构编号
    private String institutionID;
    //支付交易流水号
    private String paymentNo;
    //交易状态
    //10=处理中
    //20=支付成功
    //30=支付失败
    private int status;
    //分账模式：默认按 10=不分帐处理
    //10=不分帐
    //20=按比例分账
    //30=按金额分账
    private String splitType;
    //分账结果
    private String splitResult;
    //银行处理时间
    private String bankTxTime;
    //响应代码
    private String responseCode;
    //响应消息
    private String responseMessage;

    public PaymentResponse(String responseMessage, String responseSignature,  Key key){
        super(responseMessage, responseSignature,key);
    }

    @Override
    @SneakyThrows
    protected void process(Document document) {
        if ("2000".equals(super.code)) {
            this.institutionID = XmlUtil.getNodeText(document, "InstitutionID");
            this.paymentNo = XmlUtil.getNodeText(document, "PaymentNo");
            this.status = Integer.parseInt(XmlUtil.getNodeText(document, "Status"));
            this.splitType = XmlUtil.getNodeText(document, "SplitType");
            this.splitResult = XmlUtil.getNodeText(document, "SplitResult");
            this.bankTxTime = XmlUtil.getNodeText(document, "BankTxTime");
            this.responseCode = XmlUtil.getNodeText(document, "ResponseCode");
            this.responseMessage = XmlUtil.getNodeText(document, "ResponseMessage");
        }
    }
}
