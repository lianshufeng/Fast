package com.fast.dev.pay.server.core.cpcn.param.request;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import lombok.Data;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@Data
public class RefundRequest extends BaseRequest {

    //机构编号
    private String institutionID;
    //交易编码
    private String txCode = "2521";
    //退款交易流水号
    private String txSN;
    //原支付交易流水号（即 2511 接口中的 PaymentNo）
    private String paymentNo;
    //退款金额，单位
    private long amount;
    //退款结算标识
    private String settlementFlag;
    //备注
    private String remark;
    //原支付交易时间，格式：yyyyMMdd
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
        Element TxSN = document.createElement("TxSN");
        Element PaymentNo = document.createElement("PaymentNo");
        Element Amount = document.createElement("Amount");
        Element SettlementFlag = document.createElement("SettlementFlag");
        Element Remark = document.createElement("Remark");
        Element SourceTxTime = document.createElement("SourceTxTime");
        document.appendChild(Request);
        Request.appendChild(Head);
        Request.appendChild(Body);
        Head.appendChild(TxCode);
        TxCode.setTextContent(this.txCode);
        Head.appendChild(InstitutionID);
        InstitutionID.setTextContent(this.institutionID);
        Request.appendChild(Body);
        Body.appendChild(TxSN);
        TxSN.setTextContent(this.txSN);
        Body.appendChild(PaymentNo);
        PaymentNo.setTextContent(this.paymentNo);
        Body.appendChild(Amount);
        Amount.setTextContent(String.valueOf(this.amount));
        Body.appendChild(SettlementFlag);
        SettlementFlag.setTextContent(this.settlementFlag);
        Body.appendChild(Remark);
        if (this.remark != null) {
            Remark.setTextContent(this.remark);
        } else {
            Remark.setTextContent("");
        }

        Body.appendChild(SourceTxTime);
        SourceTxTime.setTextContent(this.sourceTxTime);
        this.postProcess(document,key);
    }
}
