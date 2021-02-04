package com.fast.dev.pay.client.support.alipay.callback;

import com.fast.dev.pay.client.support.BaseCallBack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付宝回调通知
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AliPayCallBack extends BaseCallBack {

    private String gmt_create;
    private String charset;
    private String seller_email;
    private String notify_time;
    private String subject;
    private String sign;
    private String buyer_id;
    private String version;
    private String notify_id;
    private String notify_type;
    private String out_trade_no;
    private String total_amount;
    private String trade_status;
    private String trade_no;


    private String auth_app_id;
    private String buyer_logon_id;
    private String app_id;
    private String sign_type;
    private String seller_id;


}
