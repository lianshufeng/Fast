package com.fast.dev.pay.server.core.cpcn.param.response;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Document;

import java.security.PublicKey;

@Data
@NoArgsConstructor
public class RefundResponse extends BaseResponse {

    public RefundResponse(String responseMessage, String responseSignature,Key key){
        super(responseMessage, responseSignature,key);
    }

    @Override
    protected void process(Document document) {

    }
}
