package com.fast.dev.pay.server.core.hb.model.req;

import lombok.Data;

@Data
public class OpenAcctReq extends BaseRequest {

    /**
     * 手机号
     */
    private String userMobile;


}
