package com.fast.dev.pay.server.core.cpcn.param.response;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import com.fast.dev.pay.server.core.cpcn.util.XmlUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.w3c.dom.Document;

@Data
public class DirectBindCardResponse extends BaseResponse {

    //机构编号
    private String institutionID;
    //绑定流水号
    private String txSNBinding;
    //交易状态
    //10=绑定处理中（支持重复验短时可返回）
    //20=绑定失败
    //30=绑定成功
    private int status;
    //响应代码
    private String responseCode;
    //响应消息
    private String responseMessage;
    //实际发卡银行 ID
    private String issueBankID;
    //卡类型：
    //10=个人借记
    //20=个人贷记
    private String issueCardType;
    //发卡机构代码
    private String issInsCode;
    //支付卡类型
    private String payCardType;
    //银行处理时间
    private String bankTxTime;

    public DirectBindCardResponse(String responseMessage, String responseSignature,  Key key){
        super(responseMessage, responseSignature,key);
    }

    @Override
    @SneakyThrows
    protected void process(Document document) {
        if ("2000".equals(super.code)) {
            this.institutionID = XmlUtil.getNodeText(document, "InstitutionID");
            this.txSNBinding = XmlUtil.getNodeText(document, "TxSNBinding");
            this.status = Integer.parseInt(XmlUtil.getNodeText(document, "Status"));
            this.responseCode = XmlUtil.getNodeText(document, "ResponseCode");
            this.responseMessage = XmlUtil.getNodeText(document, "ResponseMessage");
            this.issueBankID = XmlUtil.getNodeText(document, "IssueBankID");
            this.issueCardType = XmlUtil.getNodeText(document, "IssueCardType");
            this.issInsCode = XmlUtil.getNodeText(document, "IssInsCode");
            this.payCardType = XmlUtil.getNodeText(document, "PayCardType");
            this.bankTxTime = XmlUtil.getNodeText(document, "BankTxTime");
        }
    }
}
