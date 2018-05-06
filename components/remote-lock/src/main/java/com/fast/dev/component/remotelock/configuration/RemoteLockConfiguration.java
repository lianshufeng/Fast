package com.fast.dev.component.remotelock.configuration;

import com.fast.dev.component.remotelock.RemoteLock;
import com.fast.dev.component.remotelock.conf.LockOption;
import com.fast.dev.component.remotelock.impl.RemoteLockZooKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RemoteLockConfiguration {

    @Autowired
    private LockOption lockOption;

    @Bean
    public RemoteLock remoteLock() throws Exception {
        return new RemoteLockZooKeeper(this.lockOption);
    }

}
