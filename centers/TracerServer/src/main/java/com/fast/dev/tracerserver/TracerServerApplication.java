package com.fast.dev.tracerserver;

import com.fast.dev.acenter.annotation.EnableApplicationClient;
import com.fast.dev.core.boot.ApplicationBootSuper;
import org.springframework.boot.SpringApplication;
import zipkin.server.internal.EnableZipkinServer;


@EnableZipkinServer
@EnableApplicationClient
public class TracerServerApplication extends ApplicationBootSuper {

    /**
     * 默认入口方法
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(TracerServerApplication.class, args);
    }

}
