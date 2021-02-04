package com.fast.dev.pay.server.core.hb.model.req;

import com.fast.dev.pay.server.core.hb.type.AcctType;
import com.fast.dev.pay.server.core.hb.type.BussCode;
import com.fast.dev.pay.server.core.hb.type.ChannelName;
import lombok.Data;

@Data
public class ThawPayRefundReq extends BaseRequest {

    /**
     * 虚拟卡号
     */
    private String cardNo;

    /**
     * 绑定银行卡号
     */
    private String bindCardNo;

    /**
     * 绑定账户类型
     */
    private AcctType acctType;

    /**
     * 绑定账户是否为他行(0:本行，1：他行)
     */
    private String acctOther;

    /**
     * 开户行行号
     */
    private String bankNo;

    /**
     * 账户名称
     */
    private String actNm;

    /**
     * 手机号
     */
    private String phoneNO;

    /**
     * 证件类型
     */
    private String idType;

    /**
     * 证件号码
     */
    private String idNo;

    /**
     * 操作类型（1：冻结；2：解冻）
     */
    private String type;

    /**
     * 业务种类
     */
    private String bussCode;

    /**
     * 原交易流水号
     */
    private String orgTradeNo;

    /**
     * 原交易日期
     */
    private String orgPcsDate;

    /**
     * 操作金额
     */
    private String amt;

    /**
     * 交易摘要
     */
    private String payNote;

    /**
     * 订单详情
     */
    private String detail;

    /**
     * 网络交易平台（或APP）名称
     */
    private String internetPresence;

    /**
     * 交易渠道及发起方式
     */
    private String channelName;

    /**
     * 扣款次数
     */
    private String totalNum;

    /**
     * 扣款总金额
     */
    private String totalAmt;

}
