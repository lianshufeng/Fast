package com.fast.dev.pay.server.core.cpcn.param.request;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import lombok.Data;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@Data
public class QueryRedirectPaymentRequest extends BaseRequest{


    private String institutionID;
    private String paymentNo;
    private String sourceTxTime;

    public QueryRedirectPaymentRequest() {
        this.txCode = "2102";
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
        Element PaymentNo = document.createElement("PaymentNo");
        Element SourceTxTime = document.createElement("SourceTxTime");
        Request.setAttribute("version", "2.1");
        document.appendChild(Request);
        Request.appendChild(Head);
        Head.appendChild(TxCode);
        Request.appendChild(Body);
        Body.appendChild(InstitutionID);
        Body.appendChild(PaymentNo);
        Body.appendChild(SourceTxTime);
        TxCode.setTextContent(this.txCode);
        InstitutionID.setTextContent(this.institutionID);
        PaymentNo.setTextContent(this.paymentNo);
        SourceTxTime.setTextContent(this.sourceTxTime);
        this.postProcess(document, key);
    }
}
