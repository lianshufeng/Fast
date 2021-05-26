package com.fast.dev.pay.server.core.cpcn.param.request;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import lombok.Data;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@Data
public class RedirectPaymentRequest extends BaseRequest {

    //交易编码
    private String txCode = "2101";
    //机构编号
    private String institutionID;
    //支付交易流水号
    private String paymentNo;
    //跳转前来源:
    //10=App
    //20=H5
    //30=公众号
    //40=小程序
    private String redirectSource;
    //支付方式
    //45=H5 支付（PayType=30、31、32、33）
    //46=APP 支付（PayType=30、31、32、33）
    //47=手机迷你付（PayType=33）
    //48=手机 Pay（PayType=34、35）
    //50=JSAPI（PayType=31、32）
    //51=小程序支付（PayType=31）
    private String payWay;
    //支付类型
    //30=手机网银
    //31=微信
    //32=支付宝
    //33=银联
    //34=Apple Pay
    //35=Android Pay
    private String payType;
    //发卡行
    private String bankID;
    //UserID
    private String userID;
    //订单金额，单位：分
    private String amount;
    //支付方式限制：
    //10=不限定
    //20=仅支持借记卡
    //30=仅支持贷记卡
    private String limitPay;
    //结算标识
    private String settlementFlag;
    //分账模式
    private String splitType;
    //平台名称
    private String platformName;
    //商品名称
    private String goodsName;
    //用户 IP
    private String clientIP;
    //AppID（商户进件录入）
    private String appID;
    //OpenID
    private String openID;
    //分期控制标识
    //10-不指定分期（默认）
    //20-指定分期
    private String installmentType;
    //手续费收取模式：
    //10=商户贴息
    //20=持卡人付费
    //分期控制标识 20 时必填，且必
    //须传为商户贴息
    private String feeMode;
    //银行卡号
    //分期控制标识 20 且支付类别非
    //支付宝时银行卡号必填
    private String accountNumber;
    //分期期数
    //分期控制标识 20 指定分期时必填
    private String numberOfInstallments;
    //失效时间，默认为 10 分钟
    private String expirePeriod;
    //接收支付结果通知的 URL
    private String pageUrl;
    //备注
    private String remark;
    //扩展字段
    private String extension;



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
        Element RedirectSource = document.createElement("RedirectSource");
        Element PayWay = document.createElement("PayWay");
        Element PayType = document.createElement("PayType");
        Element BankID = document.createElement("BankID");
        Element UserID = document.createElement("UserID");
        Element Amount = document.createElement("Amount");
        Element LimitPay = document.createElement("LimitPay");
        Element SettlementFlag = document.createElement("SettlementFlag");
        Element SplitType = document.createElement("SplitType");
        Element PlatformName = document.createElement("PlatformName");
        Element GoodsName = document.createElement("GoodsName");
        Element ClientIP = document.createElement("ClientIP");
        Element AppID = document.createElement("AppID");
        Element OpenID = document.createElement("OpenID");
        Element InstallmentType = document.createElement("InstallmentType");
        Element FeeMode = document.createElement("FeeMode");
        Element AccountNumber = document.createElement("AccountNumber");
        Element NumberOfInstallments = document.createElement("NumberOfInstallments");
        Element ExpirePeriod = document.createElement("ExpirePeriod");
        Element PageUrl = document.createElement("PageUrl");
        Element Remark = document.createElement("Remark");
        Element Extension = document.createElement("Extension");
        Request.setAttribute("version", "2.1");
        document.appendChild(Request);
        Request.appendChild(Head);
        Head.appendChild(TxCode);
        Request.appendChild(Body);
        Body.appendChild(InstitutionID);
        Body.appendChild(PaymentNo);
        Body.appendChild(RedirectSource);
        Body.appendChild(PayWay);
        Body.appendChild(PayType);
        Body.appendChild(BankID);
        Body.appendChild(UserID);
        Body.appendChild(Amount);
        Body.appendChild(LimitPay);
        Body.appendChild(SettlementFlag);
        Body.appendChild(SplitType);
        Body.appendChild(PlatformName);
        Body.appendChild(GoodsName);
        Body.appendChild(ClientIP);
        Body.appendChild(AppID);
        Body.appendChild(OpenID);
        Body.appendChild(InstallmentType);
        Body.appendChild(FeeMode);
        Body.appendChild(AccountNumber);
        Body.appendChild(NumberOfInstallments);
        Body.appendChild(ExpirePeriod);
        Body.appendChild(PageUrl);
        Body.appendChild(Remark);
        Body.appendChild(Extension);
        TxCode.setTextContent(this.txCode);
        InstitutionID.setTextContent(this.institutionID);
        PaymentNo.setTextContent(this.paymentNo);
        RedirectSource.setTextContent(this.redirectSource);
        PayWay.setTextContent(this.payWay);
        PayType.setTextContent(this.payType);
        BankID.setTextContent(this.bankID);
        UserID.setTextContent(this.userID);
        Amount.setTextContent(this.amount);
        LimitPay.setTextContent(this.limitPay);
        SettlementFlag.setTextContent(this.settlementFlag);
        SplitType.setTextContent(this.splitType);
        PlatformName.setTextContent(this.platformName);
        GoodsName.setTextContent(this.goodsName);
        ClientIP.setTextContent(this.clientIP);
        AppID.setTextContent(this.appID);
        OpenID.setTextContent(this.openID);
        InstallmentType.setTextContent(this.installmentType);
        FeeMode.setTextContent(this.feeMode);
        AccountNumber.setTextContent(this.accountNumber);
        NumberOfInstallments.setTextContent(this.numberOfInstallments);
        ExpirePeriod.setTextContent(this.expirePeriod);
        PageUrl.setTextContent(this.pageUrl);
        Remark.setTextContent(this.remark);
        Extension.setTextContent(this.extension);
        this.postProcess(document, key);
    }
}
