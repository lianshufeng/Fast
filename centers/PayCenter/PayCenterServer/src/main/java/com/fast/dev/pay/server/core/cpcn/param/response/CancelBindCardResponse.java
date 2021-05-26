package com.fast.dev.pay.server.core.cpcn.param.response;

import com.fast.dev.pay.server.core.cpcn.param.GatheringItem;
import com.fast.dev.pay.server.core.cpcn.param.Key;
import com.fast.dev.pay.server.core.cpcn.util.XmlUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.security.PublicKey;
import java.util.ArrayList;

@Data
@NoArgsConstructor
public class CancelBindCardResponse extends BaseResponse {
    //机构编号
    private String institutionID;
    //解绑流水号
    private String txSNBinding;
    //原绑定流水号
    private String txSNUnBinding;
    //交易状态
    //20=解绑成功
    //30=解绑失败（等于已绑定）
    private int status;
    //响应代码
    private String responseCode;
    //响应消息
    private String responseMessage;
    //银行处理时间
    private String bankTxTime;
    //原绑定流水号
    private String relatedTxSNBinding;
    private ArrayList<GatheringItem> gatheringItemList;

    public CancelBindCardResponse(String responseMessage, String responseSignature,  Key key){
        super(responseMessage,responseSignature,key);
    }

    @Override
    @SneakyThrows
    protected void process(Document document) {
        if ("2000".equals(super.code)) {
            this.institutionID = XmlUtil.getNodeText(document, "InstitutionID");
            this.txSNUnBinding = XmlUtil.getNodeText(document, "TxSNUnBinding");
            this.txSNBinding = XmlUtil.getNodeText(document, "TxSNBinding");
            this.status = Integer.parseInt(XmlUtil.getNodeText(document, "Status"));
            this.responseCode = XmlUtil.getNodeText(document, "ResponseCode");
            this.responseMessage = XmlUtil.getNodeText(document, "ResponseMessage");
            this.bankTxTime = XmlUtil.getNodeText(document, "BankTxTime");
            this.relatedTxSNBinding = XmlUtil.getNodeText(document, "RelatedTxSNBinding");
            this.gatheringItemList = new ArrayList();
            NodeList nodeList = document.getElementsByTagName("Item");
            int len = nodeList.getLength();

            for(int i = 0; i < len; ++i) {
                Node Item = nodeList.item(i);
                String relatedTxSNBinding = XmlUtil.getChildNodeText(Item, "RelatedTxSNBinding");
                GatheringItem gatheringItem = new GatheringItem();
                gatheringItem.setRelatedTxSNBinding(relatedTxSNBinding);
                this.gatheringItemList.add(gatheringItem);
            }
        }
    }
}
