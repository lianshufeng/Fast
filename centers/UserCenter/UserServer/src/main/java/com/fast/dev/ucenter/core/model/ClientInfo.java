package com.fast.dev.ucenter.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户端信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientInfo {

    /**
     * * 远程ip
     */
    private String remoteIp;

    /**
     * 用户信息
     */
    private String userAgent;

}
