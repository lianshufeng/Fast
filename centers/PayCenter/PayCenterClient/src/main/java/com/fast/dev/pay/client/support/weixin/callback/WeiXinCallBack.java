package com.fast.dev.pay.client.support.weixin.callback;

import com.fast.dev.pay.client.support.BaseCallBack;
import com.fast.dev.pay.client.support.weixin.model.WeiXinAmount;
import com.fast.dev.pay.client.support.weixin.model.WeiXinPayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信回调
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeiXinCallBack extends BaseCallBack {

    private String id;
    private String create_time;
    private String resource_type;
    private String event_type;
    private String summary;

    private Resource resource;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Resource {
        private String original_type;
        private String algorithm;
        private String ciphertext;
        private String associated_data;
        private String nonce;
    }


}
