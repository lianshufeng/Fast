package com.fast.dev.pay.server.core.cpcn.param.request;


import com.fast.dev.pay.server.core.cpcn.param.Key;
import lombok.Data;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@Data
public class PaymentRequest extends BaseRequest {

    //机构编号
    private String institutionID;
    //交易编码
    private String txCode = "2511";
    //支付交易流水号
    private String paymentNo;
    //支付金额，单位：分
    private long amount;
    //绑定流水号（如 2501 接口的 TxSNBinding）
    private String txSNBinding;
    //分账模式
    private String splitType;
    //结算标识
    private String settlementFlag;
    //信用卡有效期，格式 YYMM
    private String validDate;
    //信用卡背面的末 3 位数字
    private String cvn2;
    //共享机构 ID
    private String sharedInstitutionID;
    //备注
    private String remark;

    @Override
    @SneakyThrows
    public void process(Key key) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element Request = document.createElement("Request");
        Element Head = document.createElement("Head");
        Element Body = document.createElement("Body");
        Element InstitutionID = document.createElement("InstitutionID");
        Element TxCode = document.createElement("TxCode");
        Element PaymentNo = document.createElement("PaymentNo");
        Element Amount = document.createElement("Amount");
        Element TxSNBinding = document.createElement("TxSNBinding");
        Element SplitType = document.createElement("SplitType");
        Element SettlementFlag = document.createElement("SettlementFlag");
        Element ValidDate = document.createElement("ValidDate");
        Element CVN2 = document.createElement("CVN2");
        Element SharedInstitutionID = document.createElement("SharedInstitutionID");
        Element Remark = document.createElement("Remark");
        Element UserID = document.createElement("UserID");
        document.appendChild(Request);
        Request.appendChild(Head);
        Request.appendChild(Body);
        Head.appendChild(TxCode);
        TxCode.setTextContent(this.txCode);
        Head.appendChild(InstitutionID);
        InstitutionID.setTextContent(this.institutionID);
        Request.appendChild(Body);
        Body.appendChild(PaymentNo);
        PaymentNo.setTextContent(this.paymentNo);
        Body.appendChild(Amount);
        Amount.setTextContent(String.valueOf(this.amount));
        Body.appendChild(TxSNBinding);
        TxSNBinding.setTextContent(this.txSNBinding);
        Body.appendChild(SplitType);
        SplitType.setTextContent(this.splitType);
        Body.appendChild(SettlementFlag);
        SettlementFlag.setTextContent(this.settlementFlag);
        Body.appendChild(ValidDate);
        ValidDate.setTextContent(this.validDate);
        Body.appendChild(CVN2);
        CVN2.setTextContent(this.cvn2);
        Body.appendChild(SharedInstitutionID);
        SharedInstitutionID.setTextContent(this.sharedInstitutionID);
        Body.appendChild(Remark);
        if (this.remark != null) {
            Remark.setTextContent(this.remark);
        } else {
            Remark.setTextContent("");
        }
        this.postProcess(document,key);
    }
}
