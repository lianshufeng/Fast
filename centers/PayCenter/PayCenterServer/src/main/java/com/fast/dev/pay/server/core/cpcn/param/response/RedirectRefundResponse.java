package com.fast.dev.pay.server.core.cpcn.param.response;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import com.fast.dev.pay.server.core.cpcn.util.XmlUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.w3c.dom.Document;

@Data
public class RedirectRefundResponse extends BaseResponse {

    private String status;
    private String responseCode;
    private String responseMessage;


    public RedirectRefundResponse(String responseMessage, String responseSignature,  Key key){
        super(responseMessage, responseSignature,key);
    }

    @Override
    @SneakyThrows
    protected void process(Document document) {

        if ("2000".equals(super.code)) {
            this.status = XmlUtil.getNodeText(document, "Status");
            this.responseCode = XmlUtil.getNodeText(document, "ResponseCode");
            this.responseMessage = XmlUtil.getNodeText(document, "ResponseMessage");
        }

    }
}
