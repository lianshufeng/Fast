package com.fast.dev.pay.server.core.hb.model.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HBReq {

    /**
     * 版本
     */
    private String version = "1.0";

    /**
     * appid
     */
    private String appid;

    /**
     * 签名
     */
    private String signature;

    /**
     * 参数体
     */
    private String body;
}
