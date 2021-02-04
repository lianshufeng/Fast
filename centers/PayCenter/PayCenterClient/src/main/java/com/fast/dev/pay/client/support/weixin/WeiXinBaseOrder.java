package com.fast.dev.pay.client.support.weixin;

import com.fast.dev.core.util.token.TokenUtil;
import com.fast.dev.pay.client.support.BasePaySupport;
import com.fast.dev.pay.client.support.weixin.model.WeiXinAmount;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信的基本订单属性
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class WeiXinBaseOrder extends BasePaySupport {


    //获取接口地址
    @JsonIgnore(value = true)
    public abstract String getUrl();

    //公众号ID
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String appid;

    //直连商户号
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String mchid;

    //商品描述
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String description;

    //商户订单号
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String out_trade_no;

    //交易结束时间
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String time_expire;

    //附加数据 : 附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String attach;

    //通知地址
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String notify_url;

    //订单金额信息
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private WeiXinAmount amount;


}
