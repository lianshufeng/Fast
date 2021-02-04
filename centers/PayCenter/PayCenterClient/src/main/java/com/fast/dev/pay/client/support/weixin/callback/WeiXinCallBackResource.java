package com.fast.dev.pay.client.support.weixin.callback;

import com.fast.dev.pay.client.support.weixin.model.WeiXinAmount;
import com.fast.dev.pay.client.support.weixin.model.WeiXinPayer;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class WeiXinCallBackResource {
    //公众号ID
    private String appid;
    //直连商户号
    private String mchid;
    //商户订单号
    private String out_trade_no;
    //微信支付订单号
    private String transaction_id;
    //交易类型
    private String trade_type;
    //交易状态
    private String trade_state;
    //交易状态描述
    private String trade_state_desc;
    //付款银行
    private String bank_type;
    //附加数据
    private String attach;
    //支付完成时间
    private String success_time;
    //微信支付者信息
    private WeiXinPayer combine_payer_info;
    //订单金额信息
    private WeiXinAmount amount;
}
