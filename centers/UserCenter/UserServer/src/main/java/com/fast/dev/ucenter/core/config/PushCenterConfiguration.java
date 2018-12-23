package com.fast.dev.ucenter.core.config;

import com.fast.dev.pushcenter.manager.helper.SendPushMessageHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 作者：练书锋
 * 时间：2018/9/3
 * <p>
 * 推送中心的配置
 */


@Configuration
@ComponentScan({"com.fast.dev.pushcenter.manager"})
public class PushCenterConfiguration {

//    @Bean("userSendPushMessageHelper")
//    public SendPushMessageHelper sendPushMessageHelper() {
//        return new SendPushMessageHelper();
//    }

}
