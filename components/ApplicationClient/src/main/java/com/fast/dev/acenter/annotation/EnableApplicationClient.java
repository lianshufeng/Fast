package com.fast.dev.acenter.annotation;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 应用客户端注解
 */

@EnableFeignClients
@EnableEurekaClient
public @interface EnableApplicationClient {

}
