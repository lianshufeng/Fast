package com.fast.dev.pay.server.core.cpcn.param.request;

import com.fast.dev.pay.server.core.cpcn.param.Key;
import com.fast.dev.pay.server.core.cpcn.util.SignUtil;
import com.fast.dev.pay.server.core.cpcn.util.StringUtil;
import com.fast.dev.pay.server.core.cpcn.util.XmlUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.bouncycastle.util.encoders.Base64;
import org.w3c.dom.Document;

@Data
public abstract class BaseRequest {

    protected String txCode;
    protected String requestPlainText;
    protected String requestMessage;
    protected String requestSignature;




    public abstract void process(Key key);

    @SneakyThrows
    protected void postProcess(Document document, Key key) {
        this.requestPlainText = XmlUtil.createPrettyFormat(document).trim();
        byte[] data = this.requestPlainText.getBytes("UTF-8");
        this.requestMessage = new String(Base64.encode(data));
        byte[] signature = SignUtil.sign(key.getPrivateKey(),this.requestMessage);
        this.requestSignature = StringUtil.bytes2hex(signature);
    }
}
