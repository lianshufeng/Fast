package com.fast.dev.acenter.annotation;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.turbine.TurbineHttpConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 应用客户端注解
 *
 *  @EnableCircuitBreaker , @EnableTurbine
 */



//@EnableTurbine
@Import({TurbineHttpConfiguration.class})

@EnableCircuitBreaker


// zipkin




@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableApplicationMonitorClient {

}
