package com.fast.dev.pay.server.core.hb.model.req;

import com.fast.dev.pay.server.core.hb.type.BussCode;
import com.fast.dev.pay.server.core.hb.type.ChannelName;
import lombok.Data;

@Data
public class FreezeAcctReq extends BaseRequest {

    /**
     * 虚拟卡号
     */
    private String cardNo;

    /**
     * 账户名称
     */
    private String actNm;

    /**
     * 手机号
     */
    private String phoneNo;

    /**
     * 操作金额
     */
    private String frzAmt;

    /**
     * 冻结到期日
     */
    private String hlExDt;

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
     * 冻结原因
     */
    private String frzReason;

    /**
     * 网络交易平台（或APP）名称
     */
    private String internetPresence;

    /**
     * 交易渠道及发起方式
     */
    private String channelName;

}
