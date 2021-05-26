package com.fast.dev.pay.server.core.cpcn.param.request;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import lombok.Data;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@Data
public class ConfirmBindCardRequest extends BaseRequest {

    //机构编号
    private String txCode = "2532";
    //交易编码
    private String institutionID;
    //绑定流水号
    private String txSNBinding;
    //短信验证码
    private String SMSValidationCode;
    //信用卡有效期，格式 YYMM
    private String validDate;
    //信用卡背面的末 3 位数字
    private String cvn2;


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
        Element SMSValidationCode = document.createElement("SMSValidationCode");
        Element ValidDate = document.createElement("ValidDate");
        Element CVN2 = document.createElement("CVN2");
        document.appendChild(Request);
        Request.appendChild(Head);
        Head.appendChild(InstitutionID);
        InstitutionID.setTextContent(this.institutionID);
        Head.appendChild(TxCode);
        TxCode.setTextContent(this.txCode);
        Request.appendChild(Body);
        Body.appendChild(TxSNBinding);
        TxSNBinding.setTextContent(this.txSNBinding);
        Body.appendChild(SMSValidationCode);
        SMSValidationCode.setTextContent(this.SMSValidationCode);
        Body.appendChild(ValidDate);
        ValidDate.setTextContent(this.validDate);
        Body.appendChild(CVN2);
        CVN2.setTextContent(this.cvn2);
        this.postProcess(document,key);
    }
}
