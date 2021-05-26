package com.fast.dev.pay.server.core.cpcn.param.request;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import lombok.Data;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@Data
public class QueryBindCardRequest extends BaseRequest {

    //交易编码
    private String txCode = "2504";
    //机构编号
    private String institutionID;
    //绑定流水号
    private String accountNumber;

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
        Element AccountNumber = document.createElement("AccountNumber");
        document.appendChild(Request);
        Request.appendChild(Head);
        Request.appendChild(Body);
        Head.appendChild(TxCode);
        TxCode.setTextContent(this.txCode);
        Request.appendChild(Body);
        Body.appendChild(InstitutionID);
        InstitutionID.setTextContent(this.institutionID);
        Body.appendChild(AccountNumber);
        AccountNumber.setTextContent(this.accountNumber);
        this.postProcess(document,key);
    }
}
