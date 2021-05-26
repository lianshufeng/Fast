package com.fast.dev.pay.server.core.cpcn.param.request;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import lombok.Data;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@Data
public class PreBindCardRequest extends BaseRequest {


    //机构编号
    private String txCode = "2531";
    //交易编码
    private String institutionID;
    //绑定流水号
    private String txSNBinding;
    //银行ID，参考《银行编码表》
    private String bankID;
    //账户名称
    private String accountName;
    //账户号码
    private String accountNumber;
    //开户证件类型
    private String identificationType;
    //证件号码
    private String identificationNumber;
    //手机号
    private String phoneNumber;
    //卡类型:10=个人借记,20=个人贷记
    private String cardType;
    //信用卡有效期，格式YYMM
    private String validDate;
    //信用卡背面的末 3 位数字
    private String cvn2;

    @Override
    @SneakyThrows
    public void process(Key key){
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element Request = document.createElement("Request");
        Element Head = document.createElement("Head");
        Element Body = document.createElement("Body");
        Element TxCode = document.createElement("TxCode");
        Element InstitutionID = document.createElement("InstitutionID");
        Element TxSNBinding = document.createElement("TxSNBinding");
        Element BankID = document.createElement("BankID");
        Element AccountName = document.createElement("AccountName");
        Element AccountNumber = document.createElement("AccountNumber");
        Element IdentificationType = document.createElement("IdentificationType");
        Element IdentificationNumber = document.createElement("IdentificationNumber");
        Element PhoneNumber = document.createElement("PhoneNumber");
        Element CardType = document.createElement("CardType");
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
        Body.appendChild(BankID);
        BankID.setTextContent(this.bankID);
        Body.appendChild(AccountName);
        AccountName.setTextContent(this.accountName);
        Body.appendChild(AccountNumber);
        AccountNumber.setTextContent(this.accountNumber);
        Body.appendChild(IdentificationType);
        IdentificationType.setTextContent(this.identificationType);
        Body.appendChild(IdentificationNumber);
        IdentificationNumber.setTextContent(this.identificationNumber);
        Body.appendChild(PhoneNumber);
        PhoneNumber.setTextContent(this.phoneNumber);
        Body.appendChild(CardType);
        CardType.setTextContent(this.cardType);
        if (this.validDate != null) {
            Body.appendChild(ValidDate);
            ValidDate.setTextContent(this.validDate);
        }

        if (this.cvn2 != null) {
            Body.appendChild(CVN2);
            CVN2.setTextContent(this.cvn2);
        }
        this.postProcess(document,key);
    }
}
