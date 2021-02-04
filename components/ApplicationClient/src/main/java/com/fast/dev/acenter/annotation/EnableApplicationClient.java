package com.fast.dev.acenter.annotation;

import com.fast.dev.acenter.core.config.EurekaRegisterConfig;
import com.fast.dev.acenter.core.config.FeignConfig;
import com.fast.dev.acenter.core.config.RestTemplateConfig;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 应用客户端注解
 */


//引用的非公开变量不能继承
@EnableFeignClients
@EnableHystrix
//@Import({org.springframework.cloud.openfeign.FeignClientsRegistrar.class})


//@EnableEurekaClient
@Inherited

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented

//载入RestTemplate 配置
@Import({RestTemplateConfig.class, EurekaRegisterConfig.class, FeignConfig.class})
public @interface EnableApplicationClient {

}
