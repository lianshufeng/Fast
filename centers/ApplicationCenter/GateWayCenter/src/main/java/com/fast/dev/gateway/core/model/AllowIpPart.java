package com.fast.dev.gateway.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AllowIpPart {

    //开始的ip
    private long beforeIp;

    //结束的ip
    private long afterIp;

    public AllowIpPart(long beforeIp, long afterIp) {
        this.beforeIp = beforeIp;
        this.afterIp = afterIp;
    }


}
