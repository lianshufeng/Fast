package com.fast.dev.pay.client.support.weixin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeiXinSceneInfo {

    //用户终端ip(必填)
    private String payer_client_ip;

    //商户端设备号
    private String device_id;

    //h5场景信息 (必填)
    private WeiXinH5Info h5_info;

}
