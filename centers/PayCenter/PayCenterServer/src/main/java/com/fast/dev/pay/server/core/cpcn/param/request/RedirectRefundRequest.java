package com.fast.dev.pay.server.core.cpcn.param.request;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import lombok.Data;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@Data
public class RedirectRefundRequest extends BaseRequest {

    private String institutionID;
    private String refundNo;
    private String paymentNo;
    private String amount;
    private String settlementFlag;
    private String remark;
    private String sourceTxTime;

    public RedirectRefundRequest() {
        this.txCode = "2111";
    }


    @Override
    @SneakyThrows
    public void process(Key key) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element Request = document.createElement("Request");
        Element Head = document.createElement("Head");
        Element Body = document.createElement("Body");
        Element TxCode = document.createElement("TxCode");
        Element InstitutionID = document.createElement("InstitutionID");
        Element RefundNo = document.createElement("RefundNo");
        Element PaymentNo = document.createElement("PaymentNo");
        Element Amount = document.createElement("Amount");
        Element SettlementFlag = document.createElement("SettlementFlag");
        Element Remark = document.createElement("Remark");
        Element SourceTxTime = document.createElement("SourceTxTime");
        Request.setAttribute("version", "2.1");
        document.appendChild(Request);
        Request.appendChild(Head);
        Head.appendChild(TxCode);
        Request.appendChild(Body);
        Body.appendChild(InstitutionID);
        Body.appendChild(RefundNo);
        Body.appendChild(PaymentNo);
        Body.appendChild(Amount);
        Body.appendChild(SettlementFlag);
        Body.appendChild(Remark);
        Body.appendChild(SourceTxTime);
        TxCode.setTextContent(this.txCode);
        InstitutionID.setTextContent(this.institutionID);
        PaymentNo.setTextContent(this.paymentNo);
        RefundNo.setTextContent(this.refundNo);
        Amount.setTextContent(this.amount);
        SettlementFlag.setTextContent(this.settlementFlag);
        Remark.setTextContent(this.remark);
        SourceTxTime.setTextContent(this.sourceTxTime);
        this.postProcess(document, key);
    }
}
