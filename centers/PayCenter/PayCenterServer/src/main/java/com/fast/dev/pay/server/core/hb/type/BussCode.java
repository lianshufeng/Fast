package com.fast.dev.pay.server.core.hb.type;

import lombok.Getter;

public enum BussCode {

    //商户消费
    PurchaseOfPhysicalGoods("实物商品购买", "100001"),
    VirtualGoodsPurchase("虚拟商品购买", "100002"),
    PrepaidAccountRecharge("预付费类账户充值", "100003"),
    AirBusinessTravelConsumption("航空商旅消费", "100004"),
    EventTicketBooking("活动票务订购", "100005"),
    ConsumptionOfLivingAndBusinessServices("生活及商业服务消费", "100006"),
    OtherConsumption("其他消费", "100007"),

    //公共及便民服务
    PaymentOfWaterElectricityAndCoal("水电煤缴纳", "110001"),
    PaymentOfCommunicationAndCableTVFee("通讯及有线电视费缴纳（话费、宽带、有线电视费等）", "110002"),
    GovernmentServicePayment("政府服务缴费", "110003"),
    ReleaseOfGovernmentServiceFunds("政府服务资金发放（社保、养老金、人才补贴等）", "110004"),
    NonProfitEducationPayment("非盈利教育缴费", "110005"),
    ProfitOrientedTraining("盈利性教育培训", "110006"),
    MedicalServicePayment("医疗服务缴费", "110007"),
    PropertyRentPayment("物业/租金缴费", "110008"),
    CharitableDonations("公益捐款", "110009"),
    PublicTransportPayment("公共交通缴费", "110010"),
    PaymentOfMembershipFee("会费缴纳", "110011"),
    PaymentOfFine("罚款缴纳（交通罚款等）", "110012"),
    SalaryPayment("薪资发放", "110013"),
    OtherPublicConvenienceServices("其他公共便民服务", "110014"),


    //金融服务
    FundPurchase("基金购买", "120001"),
    InvestmentAndFinancing("投资理财", "120002"),
    PremiumPayment("保费缴纳", "120003"),
    LoanRepayment("贷款还款", "120004"),
    CreditCardRepaymentTransferOut("信用卡还款转出", "120005"),
    OtherFinancialPayments("其他金融付款", "120006"),
    FundRedemptionReturnDividend("基金赎回/返还/分红", "120007"),
    InsuranceClaimsDividends("保险理赔/分红", "120008"),
    InvestmentRedemptionReturnDividend("投资赎回/返还/分红", "120009"),
    CreditGranting("信贷发放", "120010"),
    CreditCardRepaymentTransferIn("信用卡还款转入", "120011"),
    OtherFinancialReceipts("其他金融收款", "120012"),


    //资金归集
    CapitalCollectionOfChainGroupEnterprises("连锁/集团企业资金归集", "130001"),
    CollectionOfOtherFunds("其他资金归集", "130002"),

    //商户结算
    MerchantSettlement("商户结算", "140001"),
    MarketingReturn("营销返现", "140002"),
    OtherSettlement("其他结算", "140003"),


    ;

    @Getter
    private String remark;
    @Getter
    private String code;

    BussCode(String remark, String code) {
        this.remark = remark;
        this.code = code;
    }
}
