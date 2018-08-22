package com.fast.dev.ucenter.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者：练书锋
 * 时间：2018/8/21
 * 登陆的环境
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginEnvironment {


    /**
     * 登陆ip
     */
    private String remoteIp;


    /**
     * 用户的ip
     */
    private String clientIp;


    /**
     * 设备信息
     */
    private DeviceInfo device;


    /**
     * 登陆来源系统(App)标识
     */
    private String from;


}
