package com.fast.dev.component.remotelock.configuration;

import com.fast.dev.component.remotelock.RemoteLock;
import com.fast.dev.component.remotelock.conf.LockOption;
import com.fast.dev.component.remotelock.impl.RemoteLockZooKeeper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class RemoteLockConfiguration {

    @Resource
    private LockOption lockOption;

    @Bean
    public RemoteLock remoteLock() throws Exception {
        return new RemoteLockZooKeeper(this.lockOption);
    }

}
