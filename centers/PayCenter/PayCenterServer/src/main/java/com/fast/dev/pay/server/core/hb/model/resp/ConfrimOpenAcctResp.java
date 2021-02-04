package com.fast.dev.pay.server.core.hb.model.resp;

import com.fast.dev.pay.server.core.hb.type.Status;
import lombok.Data;

@Data
public class ConfrimOpenAcctResp extends HBResp{


    /**
     * 用户名
     */
    private String userName;

    /**
     * 开立账户类型
     */
    private String accountType;

    /**
     * 用户手机号
     */
    private String userMobile;

    /**
     * 虚拟卡号
     */
    private String cardNo;

    /**
     * 开户行号
     */
    private String accountNodeNo;

    /**
     * 客户号
     */
    private String custNo;
}
