package com.fast.dev.pushcenter.core.config;

import com.fast.dev.pushcenter.manager.helper.SendPushMessageHelper;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 作者：练书锋
 * 时间：2018/9/3
 * <p>
 * 推送中心的配置
 */


@Configuration
@EnableFeignClients("com.fast.dev.pushcenter.core.service.remote")
public class PushCenterConfiguration {

    @Bean
    public SendPushMessageHelper sendPushMessageHelper() {
        return new SendPushMessageHelper();
    }

}
