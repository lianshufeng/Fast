package com.fast.dev.pay.server.core.cpcn.param.request;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import lombok.Data;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@Data
public class CancelBindCardRequest extends BaseRequest {

    //机构编号
    private String txCode = "2503";
    //交易编码
    private String institutionID;
    //解绑流水号
    private String txSNUnBinding;
    //原绑定流水号
    private String txSNBinding;
    //解绑类型
    private String unBindingType;


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
        Element TxSNBinding = document.createElement("TxSNBinding");
        Element TxSNUnBinding = document.createElement("TxSNUnBinding");
        Element UnBindingType = document.createElement("UnBindingType");
        document.appendChild(Request);
        Request.appendChild(Head);
        Request.appendChild(Body);
        Head.appendChild(InstitutionID);
        InstitutionID.setTextContent(this.institutionID);
        Head.appendChild(TxCode);
        TxCode.setTextContent(this.txCode);
        Request.appendChild(Body);
        Body.appendChild(TxSNUnBinding);
        TxSNUnBinding.setTextContent(this.txSNUnBinding);
        Body.appendChild(TxSNBinding);
        TxSNBinding.setTextContent(this.txSNBinding);
        Body.appendChild(UnBindingType);
        UnBindingType.setTextContent(this.unBindingType);
        this.postProcess(document,key);
    }
}
