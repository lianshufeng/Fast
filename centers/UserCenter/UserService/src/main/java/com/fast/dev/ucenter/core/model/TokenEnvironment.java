package com.fast.dev.ucenter.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 * 令牌环境
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenEnvironment implements Serializable {


    /**
     * 客户端信息
     */
    private ClientInfo clientInfo;


    /**
     * 设备信息
     */
    private DeviceInfo device;


    /**
     * 登陆来源系统(App)标识
     */
    private String app;


}
