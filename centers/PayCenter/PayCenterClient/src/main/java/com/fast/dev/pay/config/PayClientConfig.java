package com.fast.dev.pay.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@ComponentScan("com.fast.dev.pay.client")
@EnableFeignClients("com.fast.dev.pay.client.service")
@Configuration
public class PayClientConfig {


}
