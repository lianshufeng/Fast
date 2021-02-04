package com.fast.dev.pay.server.core.hb.model.req;

import com.fast.dev.pay.server.core.hb.type.AcctType;
import lombok.Data;

@Data
public class ConfrimOpenAcctReq extends BaseRequest {

    /**
     * 原交易流水号
     */
    private String orgTradeNo;

    /**
     * 原交易日期
     */
    private String orgPcsDate;

    /**
     * 绑定账户
     */
    private String bindCardNo;

    /**
     * 证件号
     */
    private String idNo;

    /**
     * 证件类型
     */
    private String idType;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户手机号
     */
    private String userMobile;

    /**
     * 开立账户类型
     */
    private String accountType;

    /**
     * 身份证头像面照
     */
    private String certimageface;

    /**
     * 身份证国徽面照
     */
    private String certimageback;

    /**
     * 身份证有效期起始日期
     */
    private String idCardStartDate;

    /**
     * 身份证有效期结束日期
     */
    private String idCardEndDate;

    /**
     * 短信验证码
     */
    private String messageNo;

    /**
     * 常住址
     */
    private String address;

    /**
     * 职业
     */
    private String jobs;

}
