package com.fast.dev.tracerserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;




@SpringBootApplication
@EnableHystrixDashboard
@EnableTurbine
public class BreakerDashboardServerApplication {

    /**
     * 默认入口方法
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(BreakerDashboardServerApplication.class, args);
    }


}
