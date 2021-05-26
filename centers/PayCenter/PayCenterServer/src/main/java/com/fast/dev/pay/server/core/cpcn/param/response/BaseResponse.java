package com.fast.dev.pay.server.core.cpcn.param.response;

import com.fast.dev.core.util.bytes.BytesUtil;
import com.fast.dev.pay.server.core.cpcn.param.Key;
import com.fast.dev.pay.server.core.cpcn.util.PFXUtil;
import com.fast.dev.pay.server.core.cpcn.util.SignUtil;
import com.fast.dev.pay.server.core.cpcn.util.XmlUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.util.encoders.Base64;
import org.w3c.dom.Document;

import java.security.PublicKey;

@Data
@NoArgsConstructor
public abstract class BaseResponse {

    protected String responseMessage;
    protected String responseSignature;
    protected String responsePlainText;
    protected String code;
    protected String message;


    @SneakyThrows
    public BaseResponse(String responseMessage, String responseSignature, Key key) {
        this.responseMessage = responseMessage;
        this.responseSignature = responseSignature;
        byte[] data = Base64.decode(responseMessage);
        this.responsePlainText = new String(data, "UTF-8");
        byte[] signature = BytesUtil.hexToBin(responseSignature);
        boolean verify = SignUtil.verify(key.getPublicKey(),data,signature);
        if (!verify){
            throw new Exception("验签失败");
        }
        Document document = XmlUtil.createDocument(this.responsePlainText);
        this.code = XmlUtil.getNodeText(document, "Code");
        this.message = XmlUtil.getNodeText(document, "Message");
        this.process(document);
    }

    protected abstract void process(Document document);
}
