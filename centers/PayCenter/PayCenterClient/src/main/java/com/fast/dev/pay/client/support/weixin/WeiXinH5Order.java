package com.fast.dev.pay.client.support.weixin;

import com.fast.dev.pay.client.support.weixin.model.WeiXinSceneInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * app创建订单
 */
@Data
@NoArgsConstructor
public class WeiXinH5Order extends WeiXinBaseOrder {

    @Override
    public String getUrl() {
        return "https://api.mch.weixin.qq.com/v3/pay/transactions/h5";
    }

    //场景信息
    private WeiXinSceneInfo scene_info;


}
