package com.fast.dev.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import zipkin.server.internal.EnableZipkinServer;


@EnableTurbine
@EnableHystrixDashboard
@EnableZipkinServer
public class MonitorServerApplication {

    /**
     * 默认入口方法
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(MonitorServerApplication.class, args);
    }

}
