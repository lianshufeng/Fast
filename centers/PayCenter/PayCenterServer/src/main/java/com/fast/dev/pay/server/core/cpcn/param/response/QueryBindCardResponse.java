package com.fast.dev.pay.server.core.cpcn.param.response;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import com.fast.dev.pay.server.core.cpcn.util.XmlUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.w3c.dom.Document;

import java.security.PublicKey;

@Data
@NoArgsConstructor
public class QueryBindCardResponse extends BaseResponse{

    //机构编号
    private String institutionID;
    //绑定流水号
    private String txSNBinding;
    //是否已绑卡
    //10=是
    //20=否
    private int status;
    //账户号码
    private String accountNumber;


    public QueryBindCardResponse(String responseMessage, String responseSignature,  Key key) {
        super(responseMessage, responseSignature,key);
    }


    @Override
    @SneakyThrows
    protected void process(Document document) {
        if ("2000".equals(super.code)) {
            this.status = Integer.parseInt(XmlUtil.getNodeText(document, "Status"));
            this.institutionID = XmlUtil.getNodeText(document, "InstitutionID");
            this.accountNumber = XmlUtil.getNodeText(document, "AccountNumber");
            this.txSNBinding = XmlUtil.getNodeText(document, "TxSNBinding");
        }
    }
}
