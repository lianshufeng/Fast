package com.fast.dev.tracerserver;

import com.fast.dev.core.boot.ApplicationBootSuper;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;



//@EnableZipkinServer
//@EnableApplicationClient
@EnableTurbine
@EnableHystrixDashboard
public class TurbineServerApplication extends ApplicationBootSuper {

    /**
     * 默认入口方法
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(TurbineServerApplication.class, args);
    }

}
