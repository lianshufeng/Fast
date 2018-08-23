package com.fast.dev.ucenter.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.security.DenyAll;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 */
@ConfigurationProperties(prefix = "ucenter")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCenterConfig {

    /**
     *  手机验证长度
     */
    private int phoneValidataLength = 6;


    /**
     * 图形验证码长度
     */
    private int imageValidataLength=4;


    /**
     * 是否调试模式
     */
    private boolean debug;






}
