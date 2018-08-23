package com.fast.dev.ucenter.core.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.security.DenyAll;
import java.io.Serializable;

/**
 * 作者：练书锋
 * 时间：2018/8/23
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "ucenter")
public class UserCenterConf implements Serializable {

    /**
     * 手机验证长度
     */
    private int phoneValidataLength = 6;

    /**
     * 是否调试模式
     */
    private boolean phoneValidataDebug;


    /**
     * 图形验证码长度
     */
    private int imageValidataLength = 4;


}
