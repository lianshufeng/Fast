package com.fast.dev.pay.server.core.cpcn.param.request;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import lombok.Data;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@Data
public class QueryPaymentResultRequest extends BaseRequest {


    //机构编号
    private String institutionID;
    //交易编码
    private String txCode = "2512";
    //支付交易流水号
    private String paymentNo;
    //原支付交易时间
    private String sourceTxTime;

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
        Element SourceTxTime = document.createElement("SourceTxTime");
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
        Body.appendChild(SourceTxTime);
        SourceTxTime.setTextContent(this.sourceTxTime);
        this.postProcess(document,key);
    }
}
