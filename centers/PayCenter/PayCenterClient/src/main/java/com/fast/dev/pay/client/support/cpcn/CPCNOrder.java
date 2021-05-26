package com.fast.dev.pay.client.support.cpcn;

import com.fast.dev.pay.client.support.BasePaySupport;
import com.fast.dev.pay.client.type.cpcn.PayType;
import com.fast.dev.pay.client.type.cpcn.PayWay;
import com.fast.dev.pay.client.type.cpcn.RedirectSourceType;
import lombok.Data;

/**
 * 中金支付订单
 */
@Data
public class CPCNOrder extends BasePaySupport {

    //跳转前来源:
    private RedirectSourceType redirectSource;
    //支付方式
    private PayWay payWay;
    //支付类型
    private PayType payType;
    //结算标识
    private String splitResult;

    //平台名称
    private String platformName;
    //用户 IP
    private String clientIP;
    //AppID（商户进件录入）
    private String appID;
    //OpenID
    private String openID;

    //备注
    private String remark;
    //扩展字段
    private String extension;

    private String userID;


}


